package com.dosion.noisense.module.api.service;

import com.dosion.noisense.module.api.entity.AutonomousDistrictEntity;
import com.dosion.noisense.module.api.repository.AutonomousDistrictRepository;
import com.dosion.noisense.web.api.controller.SensorDataApiReader;
import com.dosion.noisense.web.api.dto.SensorDataApiDTO;
import com.dosion.noisense.module.api.entity.SensorDataApiEntity;
import com.dosion.noisense.module.api.repository.SensorDataRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SensorDataApiService {

  private final SensorDataApiReader sensorDataApiReader;
  private final AutonomousDistrictRepository autonomousDistrictRepository;
  private final SensorDataRepository sensorDataRepository;
  private final ObjectMapper objectMapper;
  private static final int CHUNK_SIZE = 1000;

  // 최초 실행 작업이 진행 중인지 여부를 나타내는 플래그
  private final AtomicBoolean isInitialLoadRunning = new AtomicBoolean(false);

  // API 호출로 직접 최초 실행 -> 최초에만 모든 데이터 가져올 수 있게
  // 비동기 사용으로 더 빠르게
  @Async
  @Transactional
  public void fetchAllHistoricalData() {

    log.info("S-DoT 전체 데이터 일괄 수집 작업을 시작합니다 (자치구 구분 없음).");

    if (isInitialLoadRunning.getAndSet(true)) {
      log.warn("이미 최초 데이터 수집 작업이 진행 중입니다. 새로운 요청을 무시합니다.");
      return;
    }

    int totalCount;
    try {
      totalCount = sensorDataApiReader.getTotalCount();
      if (totalCount == 0) {
        log.warn("API에서 가져올 데이터가 없습니다. 작업을 종료합니다.");
        return;
      }
      log.info("API로부터 총 {}건의 데이터를 수집합니다.", totalCount);
    } catch (Exception e) {
      log.error("API에서 전체 데이터 건수를 가져오는 데 실패했습니다. 작업을 중단합니다.", e);
      return;
    }

    // 전체 건수를 기준으로 CHUNK_SIZE 만큼 페이지네이션 수행
    for (int startIndex = 1; startIndex <= totalCount; startIndex += CHUNK_SIZE) {
      int endIndex = startIndex + CHUNK_SIZE - 1;
      log.info("데이터 수집 중... ({} ~ {})", startIndex, endIndex);
      try {
        // 자치구 이름(null)으로 API를 호출하여 전체 데이터를 가져옵니다.
        String jsonResponse = sensorDataApiReader.callApiForDistrict(null, startIndex, endIndex).block();
        List<SensorDataApiDTO> dtoList = parseResponse(jsonResponse);

        if (dtoList.isEmpty()) {
          log.warn("해당 페이지({} ~ {})에 데이터가 없습니다. 다음 페이지로 넘어갑니다.", startIndex, endIndex);
          continue;
        }

        // saveNewData의 로그 출력을 위해 "전체 일괄"이라는 가상 이름을 전달
        saveNewData(dtoList, "전체 일괄");

      } catch (Exception e) {
        log.error("데이터 페이지({} ~ {}) 처리 중 오류 발생. 해당 페이지를 건너뛰고 계속합니다.", startIndex, endIndex, e);
      }
    }
    log.info("S-DoT 전체 데이터 일괄 수집 작업이 성공적으로 완료되었습니다.");
  }

  // 주기적으로 가져올 자치구 별 데이터
  @Transactional
  public void fetchRecentData() {

    // 메서드 시작 시점에 플래그 확인
    if (isInitialLoadRunning.get()) {
      log.info("최초 데이터 수집 작업이 진행 중이므로, 정기 스케줄을 건너뜁니다.");
      return; // 플래그가 true이면, 즉시 메서드를 종료합니다.
    }


    log.info("정기적인 S-DoT 최신 데이터 수집을 시작합니다...");
    List<AutonomousDistrictEntity> districtList = autonomousDistrictRepository.findAll();
    for (AutonomousDistrictEntity district : districtList) {
      String districtNameEn = district.getNameEn();
      String districtNameKo = district.getNameKo();
      try {
        processFirstPageForDistrict(districtNameEn, districtNameKo);
      } catch (Exception e) {
        log.error("[{}] 최신 데이터 처리 중 예외가 발생했습니다.", districtNameKo, e);
      }
    }
    log.info("정기적인 S-DoT 최신 데이터 수집이 완료되었습니다.");
  }


  // 일단 스케줄러 사용 -> 합치면서 변경 할 듯?
  @Scheduled(cron = "0 15 * * * *")
  public void scheduledBatchExecution() {
    fetchRecentData();
  }

  // 자치구 별 데이터 처리
  private void processAllPagesForDistrict(String districtNameEn, String districtNameKo) throws Exception {
    int startIndex = 1;
    boolean hasMoreData = true;
    while (hasMoreData) {
      int endIndex = startIndex + CHUNK_SIZE - 1;
      String jsonResponse = sensorDataApiReader.callApiForDistrict(districtNameEn, startIndex, endIndex).block();
      List<SensorDataApiDTO> dtoList = parseResponse(jsonResponse);

      if (dtoList.isEmpty()) {
        hasMoreData = false;
      } else {
        saveNewData(dtoList, districtNameKo);
        if (dtoList.size() < CHUNK_SIZE) {
          hasMoreData = false;
        } else {
          startIndex += CHUNK_SIZE;
        }
      }
    }
  }


  // 특정 자치구의 첫페이지 데이터만 가져옴
  private void processFirstPageForDistrict(String districtNameEn, String districtNameKo) throws Exception {
    String jsonResponse = sensorDataApiReader.callApiForDistrict(districtNameEn, 1, CHUNK_SIZE).block();
    List<SensorDataApiDTO> dtoList = parseResponse(jsonResponse);

    if (!dtoList.isEmpty()) {
      saveNewData(dtoList, districtNameKo);
    } else {
      log.info("[{}] 새롭게 추가할 데이터가 없습니다.", districtNameKo);
    }
  }

  private void saveNewData(List<SensorDataApiDTO> dtoList, String districtNameKo) {
    LocalDateTime minTime = dtoList.stream().map(SensorDataApiDTO::getSensingTime).min(LocalDateTime::compareTo).orElseThrow();
    LocalDateTime maxTime = dtoList.stream().map(SensorDataApiDTO::getSensingTime).max(LocalDateTime::compareTo).orElseThrow();
    Set<String> existingKeys = sensorDataRepository.findExistingKeys(minTime, maxTime);

    List<SensorDataApiEntity> newEntitiesToSave = dtoList.stream()
      .filter(dto -> !existingKeys.contains(dto.getAdministrativeDistrict() + ":" + dto.getSensingTime()))
      .map(this::mapDtoToEntity)
      .collect(Collectors.toList());

    if (!newEntitiesToSave.isEmpty()) {
      sensorDataRepository.saveAll(newEntitiesToSave);
      log.info("[{}] 새로운 데이터 {}건을 저장했습니다. (중복 {}건 제외)",
        districtNameKo, newEntitiesToSave.size(), dtoList.size() - newEntitiesToSave.size());
    } else {
      log.info("[{}] 새롭게 추가할 데이터가 없습니다. (중복 {}건 처리됨)", districtNameKo, dtoList.size());
    }
  }

  private SensorDataApiEntity mapDtoToEntity(SensorDataApiDTO sensorDataApiDTO) {
    return SensorDataApiEntity.builder()
      .sensingTime(sensorDataApiDTO.getSensingTime())
      .region(sensorDataApiDTO.getRegion())
      .autonomousDistrict(sensorDataApiDTO.getAutonomousDistrict())
      .administrativeDistrict(sensorDataApiDTO.getAdministrativeDistrict())
      .maxNoise(sensorDataApiDTO.getMaxNoise())
      .avgNoise(sensorDataApiDTO.getAvgNoise())
      .minNoise(sensorDataApiDTO.getMinNoise())
      .maxTemp(sensorDataApiDTO.getMaxTemp())
      .avgTemp(sensorDataApiDTO.getAvgTemp())
      .minTemp(sensorDataApiDTO.getMinTemp())
      .maxHumi(sensorDataApiDTO.getMaxHumi())
      .avgHumi(sensorDataApiDTO.getAvgHumi())
      .minHumi(sensorDataApiDTO.getMinHumi())
      .build();
  }

  private List<SensorDataApiDTO> parseResponse(String jsonResponse) throws Exception {
    if (jsonResponse == null || jsonResponse.isEmpty()) return List.of();
    JsonNode rootNode = objectMapper.readTree(jsonResponse);
    JsonNode rowNode = rootNode.path("IotVdata017").path("row");
    if (rowNode.isMissingNode() || !rowNode.isArray()) return List.of();
    return objectMapper.convertValue(rowNode, new TypeReference<>() {});
  }
}

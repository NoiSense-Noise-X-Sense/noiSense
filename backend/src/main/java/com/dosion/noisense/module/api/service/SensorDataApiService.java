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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SensorDataApiService {

  private final SensorDataApiReader sensorDataApiReader;
  private final AutonomousDistrictRepository autonomousDistrictRepository;
  private final SensorDataRepository sensorDataRepository;
  private final Executor batchTaskExecutor;
  private final ObjectMapper objectMapper;
  private static final int CHUNK_SIZE = 1000;

  private final AtomicBoolean isInitialLoadRunning = new AtomicBoolean(false);
  static final DateTimeFormatter KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

  public SensorDataApiService(
    SensorDataApiReader sensorDataApiReader,
    AutonomousDistrictRepository autonomousDistrictRepository,
    SensorDataRepository sensorDataRepository,
    ObjectMapper objectMapper,
    @Qualifier("batchTaskExecutor") Executor batchTaskExecutor
  ) {
    this.sensorDataApiReader = sensorDataApiReader;
    this.autonomousDistrictRepository = autonomousDistrictRepository;
    this.sensorDataRepository = sensorDataRepository;
    this.objectMapper = objectMapper;
    this.batchTaskExecutor = batchTaskExecutor;
  }

  @Async
  public void fetchAllHistoricalData() {

    if (isInitialLoadRunning.getAndSet(true)) {

      log.warn("이미 최초 데이터 수집 작업이 진행 중입니다.");

      return;

    }
    try {
      log.info("S-DoT 전체 데이터 병렬 수집 작업을 시작합니다.");
      int totalCount = sensorDataApiReader.getTotalCount();
      log.info("수집 할 데이터는 총 {}건 입니다.", totalCount);
      if (totalCount <= 0) {
        log.warn("API에서 가져올 데이터가 없습니다.");
        return;
      }

      List<CompletableFuture<Void>> futures = new ArrayList<>();
      for (int startIndex = 1; startIndex <= totalCount; startIndex += CHUNK_SIZE) {

        final int start = startIndex;
        final int end = Math.min(startIndex + CHUNK_SIZE - 1, totalCount);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
          processPage(start, end);
        }, this.batchTaskExecutor);
        futures.add(future);
      }
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
      log.info("S-DoT 전체 데이터 병렬 수집 작업이 성공적으로 완료되었습니다.");
    } finally {
      isInitialLoadRunning.set(false);
      log.info("최초 데이터 수집 작업 절차가 모두 종료되었습니다.");
    }
  }

  @Transactional
  public void fetchRecentData() {
    if (isInitialLoadRunning.get()) {
      log.info("최초 데이터 수집 작업이 진행 중이므로, 정기 스케줄을 건너뜁니다.");
      return;
    }

    log.info("정기적인 S-DoT 최신 데이터 수집을 시작합니다...");

    // 증분 업데이트를 위해 DB에서 가장 마지막 시간을 Optional로 조회
    Optional<LocalDateTime> lastKnownTimeOpt = sensorDataRepository.findLatestSensingTime();
    log.info("증분 업데이트 기준 시간: {}", lastKnownTimeOpt.map(Object::toString).orElse("없음(전체)"));

    List<AutonomousDistrictEntity> districtList = autonomousDistrictRepository.findAll();
    for (AutonomousDistrictEntity district : districtList) {
      String districtNameEn = district.getNameEn();
      String districtNameKo = district.getNameKo();
      try {
        // 조회한 Optional<LocalDateTime> 다음 메서드로 전달
        processFirstPageForDistrict(districtNameEn, districtNameKo, lastKnownTimeOpt);
      } catch (Exception e) {
        log.error("[{}] 최신 데이터 처리 중 예외가 발생했습니다.", districtNameKo, e);
      }
    }
    log.info("정기적인 S-DoT 최신 데이터 수집이 완료되었습니다.");
  }

  @Transactional
  public void processPage(int startIndex, int endIndex) {
    try {
      log.info("데이터 수집 중... ({} ~ {}) [Thread: {}]", startIndex, endIndex, Thread.currentThread().getName());
      String jsonResponse = sensorDataApiReader.callApiForDistrict(null, startIndex, endIndex).block();
      List<SensorDataApiDTO> dtoList = parseResponse(jsonResponse);

      if (!dtoList.isEmpty()) {
        // 전체 데이터 수집 시에는 시간 필터링이 없다는 의미로 Optional.empty()ㄴ 전달
        saveNewData(dtoList, "전체 일괄", Optional.empty());
      }
    } catch (Exception e) {
      log.error("데이터 페이지({} ~ {}) 처리 중 오류 발생. 해당 페이지를 건너뜁니다.", startIndex, endIndex, e);
    }
  }

  @Scheduled(cron = "0 15 * * * *")
  public void scheduledBatchExecution() {
    fetchRecentData();
  }

  // Optional<LocalDateTime> 받도록 함
  private void processFirstPageForDistrict(String districtNameEn, String districtNameKo, Optional<LocalDateTime> lastKnownTimeOpt) throws Exception {
    String jsonResponse = sensorDataApiReader.callApiForDistrict(districtNameEn, 1, CHUNK_SIZE).block();
    List<SensorDataApiDTO> dtoList = parseResponse(jsonResponse);

    if (!dtoList.isEmpty()) {
      // Optional을 그대로 saveNewData로 전달
      saveNewData(dtoList, districtNameKo, lastKnownTimeOpt);
    } else {
      log.info("[{}] 새롭게 추가할 데이터가 없습니다.", districtNameKo);
    }
  }

  private void saveNewData(List<SensorDataApiDTO> dtoList, String sourceName, Optional<LocalDateTime> lastKnownTimeOpt) {
    if (dtoList == null || dtoList.isEmpty()) {
      return;
    }


    List<SensorDataApiDTO> dataToProcess = lastKnownTimeOpt
      .map(lastTime -> dtoList.stream()
        .filter(dto -> dto.getSensingTime().isAfter(lastTime))
        .collect(Collectors.toList()))
      .orElse(dtoList);

    if (dataToProcess.isEmpty()) {
      log.info("[{}] 처리할 새로운 데이터가 없습니다.", sourceName);
      return;
    }

    List<SensorDataApiEntity> newEntitiesToSave = dataToProcess.stream()
      .map(this::mapDtoToEntity)
      .collect(Collectors.toList());

    if (!newEntitiesToSave.isEmpty()) {
      sensorDataRepository.bulkInsert(newEntitiesToSave);
      log.info("[{}] 새로운 데이터 {}건을 저장했습니다.", sourceName, newEntitiesToSave.size());
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

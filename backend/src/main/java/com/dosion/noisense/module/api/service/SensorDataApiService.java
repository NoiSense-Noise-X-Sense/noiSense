package com.dosion.noisense.module.api.service;

import com.dosion.noisense.module.api.entity.AutonomousDistrictEntity;
import com.dosion.noisense.module.api.repository.AutonomousDistrictRepository;
import com.dosion.noisense.web.api.controller.SensorDataApiReader;
import com.dosion.noisense.web.api.dto.SensorDataApiDTO;
import com.dosion.noisense.module.api.entity.SensorDataApiEntity;
import com.dosion.noisense.module.api.repository.SensorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

  @Transactional
  protected void fetchAndSaveSensorData() {

    int totalCount = 0;
    try {

      totalCount = sensorDataApiReader.getTotalCount();

      if (totalCount < 1) {
        log.warn("API에 불러올 데이터가 없습니다.");
        return;
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    log.info("총 {}건의 데이터를 수집합니다.", totalCount);

    List<AutonomousDistrictEntity> districtList = autonomousDistrictRepository.findAll();
    log.info("총 {}개 자치구의 데이터를 수집합니다.", districtList.size());


    for (int i = 1; i <= totalCount / CHUNK_SIZE; i += CHUNK_SIZE) {

      int startIndex = i;

      int endIndex = Math.min(i + CHUNK_SIZE - 1, totalCount);


      // 한 페이지 만큼의 데이터 호출
      String jsonResponse = sensorDataApiReader.callApiForDistrict(startIndex, endIndex);

      List<SensorDataApiDTO> dtoList = parseResponse(jsonResponse);
      if (dtoList.isEmpty()) {
        // 해당 페이지에 데이터가 없으면 다음 루프로
        continue;
      }


      // 자치구 데이터 가져오기
      for (AutonomousDistrictEntity district : districtList) {

        //로그 출력용으로 NameKo도 사용
        String districtNameEn = district.getNameEn();
        String districtNameKo = district.getNameKo();
        log.info("[{}] 데이터 처리 시작", districtNameKo);

        try {
          fetchAndSaveSensorDataForDistrict(districtNameEn, districtNameKo, 1, CHUNK_SIZE);
        } catch (Exception e) {
          log.error("[{}] 데이터에서 처리 중 예외 발생", districtNameKo, e);
          continue;
        }
      }



    }
  }

  // 자치구 분리해서 데이터 가져오기
  private void fetchAndSaveSensorDataForDistrict(String districtNameEN, String districtNameKo, int startIndex, int endIndex) {



    String jsonResponse = sensorDataApiReader.callApiForDistrict(districtNameEN, startIndex, endIndex).block();

    List<SensorDataApiDTO> dtoList = null;
    try {

      dtoList = parseResponse(jsonResponse);

      if (dtoList == null || dtoList.isEmpty()) {
        log.warn("[{}] API로부터 수신된 데이터가 없습니다.", districtNameKo);
        return;
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    LocalDateTime minTime = dtoList.stream().map(SensorDataApiDTO::getSensingTime).min(LocalDateTime::compareTo).orElseThrow();
    LocalDateTime maxTime = dtoList.stream().map(SensorDataApiDTO::getSensingTime).max(LocalDateTime::compareTo).orElseThrow();

    Set<String> existingKeys = sensorDataRepository.findExistingKeys(minTime, maxTime);

    List<SensorDataApiEntity> newEntitiesToSave = dtoList.stream()
      .filter(dto -> !existingKeys.contains(dto.getAdministrativeDistrict() + ":" + dto.getSensingTime()))
      .map(this::mapDtoToEntity) // 메서드 참조로 변경
      .collect(Collectors.toList());

    if (!newEntitiesToSave.isEmpty()) {
      sensorDataRepository.saveAll(newEntitiesToSave);
      log.info("[{}] 새로운 데이터 {}건을 저장했습니다. (중복 {}건 제외)",
        districtNameKo, newEntitiesToSave.size(), dtoList.size() - newEntitiesToSave.size());
    } else {
      log.info("[{}] 새롭게 추가할 데이터가 없습니다.", districtNameKo);
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

  @Scheduled(cron = "0 */1 * * * *")
  public void scheduledBatchExecution() {
    System.out.println("정기적인 S-DoT 데이터 배치 작업을 시작합니다...");
    fetchAndSaveSensorData();
    System.out.println("S-DoT 데이터 배치 작업이 성공적으로 완료되었습니다.");
  }



  // JSON 응답에서 실제 데이터 배열을 추출하여 DTO 리스트로 만드는 헬퍼 메서드
  private List<SensorDataApiDTO> parseResponse(String jsonResponse) throws Exception {

    if (jsonResponse == null || jsonResponse.isEmpty()) {
      return List.of();
    }

    // JSON 구조에 맞게
    com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(jsonResponse);
    com.fasterxml.jackson.databind.JsonNode rowNode = rootNode.path("IotVdata017").path("row");

    return objectMapper.convertValue(rowNode, new com.fasterxml.jackson.core.type.TypeReference<List<SensorDataApiDTO>>() {});
  }
}

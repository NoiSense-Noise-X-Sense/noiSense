package com.dosion.noisense.common.service;

import com.dosion.noisense.common.api.SensorDataApiReader;
import com.dosion.noisense.common.dto.SensorDataApiDTO;
import com.dosion.noisense.common.entity.SensorDataApiEntity;
import com.dosion.noisense.common.repository.SensorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SensorDataApiService {

  private final SensorDataApiReader sensorDataApiReader;
  private final SensorDataRepository sensorDataRepository;
  private final ObjectMapper objectMapper;
  private static final int CHUNK_SIZE = 1000;

  @Transactional
  protected void fetchAndSaveSensorData() {

    try {

      int totalCount = sensorDataApiReader.getTotalCount();
      if (totalCount < 1) {
        System.out.println("API로부터 수집된 데이터가 없습니다.");
        return;
      }

      System.out.println("총 " + totalCount + "건의 데이터에 대한 배치 작업을 시작합니다.");

      // 전체 데이터를 페이지 단위로 나눠 처리하는 반복문
      for (int i = 1; i <= totalCount; i += CHUNK_SIZE) {

        int startIndex = i;

        int endIndex = Math.min(i + CHUNK_SIZE - 1, totalCount);

        System.out.printf("데이터 처리 중... (%d ~ %d / %d)%n", startIndex, endIndex, totalCount);

        // 한 페이지 만큼의 데이터 호출
        String jsonResponse = sensorDataApiReader.callApi(startIndex, endIndex);

        List<SensorDataApiDTO> dtoList = parseResponse(jsonResponse);
        if (dtoList.isEmpty()) {
          // 해당 페이지에 데이터가 없으면 다음 루프로
          continue;

        }

        List<SensorDataApiEntity> entitiesToSave = dtoList.stream()
          .map(dto -> SensorDataApiEntity.builder()
            .sensingTime(dto.getSensingTime())
            .region(dto.getRegion())
            .autonomousDistrict(dto.getAutonomousDistrict())
            .administrativeDistrict(dto.getAdministrativeDistrict())
            .maxNoise(dto.getMaxNoise())
            .avgNoise(dto.getAvgNoise())
            .minNoise(dto.getMinNoise())
            .maxTemp(dto.getMaxTemp())
            .avgTemp(dto.getAvgTemp())
            .minTemp(dto.getMinTemp())
            .maxHumi(dto.getMaxHumi())
            .avgHumi(dto.getAvgHumi())
            .minHumi(dto.getMinHumi())
            .build())
          .collect(Collectors.toList());


        sensorDataRepository.saveAll(entitiesToSave);

      }
      System.out.println("모든 데이터 처리가 성공적으로 완료되었습니다.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Scheduled(cron = "0 0 */3 * * *")
  public void scheduledBatchExecution() {
    System.out.println("정기적인 S-DoT 데이터 배치 작업을 시작합니다...");
    fetchAndSaveSensorData(); // 내부의 protected 메서드 호출
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

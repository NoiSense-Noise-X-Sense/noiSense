package com.dosion.noisense.module.api.service;

import com.dosion.noisense.module.api.entity.AutonomousDistrictEntity;
import com.dosion.noisense.module.api.repository.AutonomousDistrictRepository;
import com.dosion.noisense.web.api.controller.SensorDataApiReader;
import com.dosion.noisense.web.api.dto.SensorDataApiDto;
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

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
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
  private @Value("${api.seoul.service-name}") String serviceName;

  // run 중 일 때 체크
  private final AtomicBoolean isInitialLoadRunning = new AtomicBoolean(false);


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
    Long startTime = System.currentTimeMillis();
    log.info("S-DoT 전체 데이터 병렬 수집 작업을 시작합니다.");
    try {


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
          processPage(start, end, startTime);
        }, this.batchTaskExecutor);
        futures.add(future);
      }

      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
      log.info("S-DoT 전체 데이터 병렬 수집 작업이 성공적으로 완료되었습니다.");

    } finally {

      // 종료 되었으니 isInitialLoadRunning 종료상태로 변경
      isInitialLoadRunning.set(false);
      log.info("최초 데이터 수집 작업 절차가 모두 종료되었습니다.");
      //소요시간 표시 메서드
      takenTimeLog(startTime);
    }

  }

  @Transactional
  public void fetchRecentData() {

    // run 중 일 때 return
    if (isInitialLoadRunning.get()) {
      log.info("최초 데이터 수집 작업이 진행 중이므로, 정기 스케줄을 건너뜁니다.");
      return;
    }

    log.info("정기적인 S-DoT 최신 데이터 수집을 시작합니다...");
    Long startTime = System.currentTimeMillis();

    // 증분 업데이트를 위해 DB에서 가장 마지막 시간을 조회
    Set<LocalDateTime> lastKnownTimes = sensorDataRepository.findLatestSensingTime();
    String timeToLog;

    // lastKnownTimes에 데이터가 아예 들어오지 않는 경우 -> 최초 데이터 없음
    if (lastKnownTimes.isEmpty() || lastKnownTimes.contains(null)) {
      log.warn("기준 시간이 없거나 유효하지 않아 전체 데이터 수집을 실행합니다.");
      // 전체 데이터 가져오기
      fetchAllHistoricalData();
    } else {
      // 있다면 증분 업데이트 실행
      LocalDateTime latestTime = lastKnownTimes.iterator().next();

      log.info("증분 업데이트를 시작합니다. 기준 시간: {}", latestTime);
      runIncrementalUpdate(lastKnownTimes, startTime);
    }
    log.info("정기적인 S-DoT 최신 데이터 수집이 완료되었습니다.");
    takenTimeLog(startTime);
  }

  // 증분 업데이트 실행
  private void runIncrementalUpdate(Set<LocalDateTime> lastKnownTimes, Long startTime) {

    List<AutonomousDistrictEntity> districtList = autonomousDistrictRepository.findAll();
    for (AutonomousDistrictEntity district : districtList) {
      String districtNameEn = district.getNameEn();
      String districtNameKo = district.getNameKo();
      try {
        processFirstPageForDistrict(districtNameEn, districtNameKo, lastKnownTimes, startTime);
      } catch (Exception e) {
        log.error("[{}] 최신 데이터 처리 중 예외가 발생했습니다.", districtNameKo, e);
      }
    }

  }

  @Transactional
  public void processPage(int startIndex, int endIndex, Long startTime) {
    try {
      log.info("데이터 수집 중... ({} ~ {}) [Thread: {}]", startIndex, endIndex, Thread.currentThread().getName());
      String jsonResponse = sensorDataApiReader.callApiForDistrict(null, startIndex, endIndex).block();
      List<SensorDataApiDto> dtoList = parseResponse(jsonResponse);

      if (!dtoList.isEmpty()) {
        // 전체 데이터 수집 시에는 시간 필터링이 없다는 의미로 null 전달
        saveNewData(dtoList, "전체 일괄", null, startTime);
      }
    } catch (Exception e) {
      log.error("데이터 페이지({} ~ {}) 처리 중 오류 발생. 해당 페이지를 건너뜁니다.", startIndex, endIndex, e);
    }
  }

  @Scheduled(cron = "0 */1 * * * *")
  public void scheduledBatchExecution() {
    fetchRecentData();
  }

  // lastKnownTimes 받도록 함
  private void processFirstPageForDistrict(String districtNameEn, String districtNameKo, Set<LocalDateTime> lastKnownTimes, Long startTime) throws Exception {
    String jsonResponse = sensorDataApiReader.callApiForDistrict(districtNameEn, 1, CHUNK_SIZE).block();
    List<SensorDataApiDto> dtoList = parseResponse(jsonResponse);

    if (!dtoList.isEmpty()) {
      // 그대로 saveNewData로 전달
      saveNewData(dtoList, districtNameKo, lastKnownTimes, startTime);
    } else {
      log.info("[{}] 새롭게 추가할 데이터가 없습니다.", districtNameKo);
    }
  }

  private void saveNewData(List<SensorDataApiDto> dtoList, String sourceName, Set<LocalDateTime> lastKnownTimes, Long startTime) {
    if (dtoList == null || dtoList.isEmpty()) {
      return;
    }

    List<SensorDataApiDto> dataToProcess;

    // 기존 데이터가 있을 경우 lastKnownTimes로 필터링
    if ( lastKnownTimes == null || lastKnownTimes.isEmpty() ) {

      dataToProcess = new ArrayList<>(dtoList);

    } else {

      dataToProcess = lastKnownTimes
        .stream().map(lastTime -> dtoList.stream()
          .filter(dto -> dto.getSensingTime().isAfter(lastTime))
          .collect(Collectors.toList()))
        .findAny().orElse(dtoList);

    }

    if (dataToProcess.isEmpty()) {
      log.info("[{}] 처리할 새로운 데이터가 없습니다.", sourceName);
      return;
    }

    // 저장
    List<SensorDataApiEntity> newEntitiesToSave = dataToProcess.stream()
      .map(this::mapDtoToEntity)
      .collect(Collectors.toList());

    if (!newEntitiesToSave.isEmpty()) {
      sensorDataRepository.bulkInsert(newEntitiesToSave);


      log.info("[{}] 새로운 데이터 {}건을 저장했습니다.", sourceName, newEntitiesToSave.size());

    }
  }

  // 정기적인 패치 중 데이터가 null인 경우 전체 데이터 넣기
  private void runFullData(long startTime) {
    try {

      processPage(1, CHUNK_SIZE, startTime);

    } catch (Exception e) {
      log.error("전체 데이터 수집 중 예외가 발생했습니다.", e);
    }
  }

  // 총 소요시간 표시 메서드
  private void takenTimeLog(long startTime) {

    long endTime = System.currentTimeMillis();
    long takenTime = endTime - startTime;

    log.info("작업이 완료되었습니다. 총 소요시간: {}", timeFormatter(takenTime));

  }


  // Millis 단위를 가독성 좋게 실제 시간으로 표기
  private String timeFormatter(long millis) {

    if (millis < 1000) {
      return String.format("%dms", millis);
    }
    long totalSeconds = millis / 1000;
    long minutes = totalSeconds / 60;
    double seconds = (millis % 60000) / 1000.0;

    if (minutes > 0) {
      return String.format("%d분 %.3f초", minutes, seconds);
    } else {
      return String.format("%.3f초", seconds);
    }

  }


  private SensorDataApiEntity mapDtoToEntity(SensorDataApiDto sensorDataApiDTO) {
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


  private List<SensorDataApiDto> parseResponse(String jsonResponse) throws Exception {

    // API 응답이 아예 없거나 비어있는 경우 진행하지 않음
    if (jsonResponse == null || jsonResponse.isEmpty()) {
      return List.of();
    }
    JsonNode rootNode = objectMapper.readTree(jsonResponse);
    JsonNode rowNode = rootNode.path(serviceName).path("row");
    if (rowNode.isMissingNode() || !rowNode.isArray()) return List.of();
    return objectMapper.convertValue(rowNode, new TypeReference<>() {});
  }
}

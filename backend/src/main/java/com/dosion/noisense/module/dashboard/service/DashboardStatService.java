package com.dosion.noisense.module.dashboard.service;

import com.dosion.noisense.module.api.repository.SensorDataRepository;
import com.dosion.noisense.module.board.elasticsearch.service.BoardEsService;
import com.dosion.noisense.module.dashboard.entity.*;
import com.dosion.noisense.module.dashboard.repository.DashboardDistrictNoiseYearlyRepository;
import com.dosion.noisense.module.dashboard.repository.DashboardDistrictNoiseHourlyRepository;
import com.dosion.noisense.module.dashboard.repository.DashboardDistrictNoiseSummaryRepository;
import com.dosion.noisense.module.dashboard.repository.DashboardDistrictNoiseZoneRepository;
import com.dosion.noisense.module.district.entity.AutonomousDistrict;
import com.dosion.noisense.module.district.repository.DistrictRepository;
import com.dosion.noisense.module.district.service.DistrictService;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardStatService {
  private final SensorDataRepository sensorDataRepository;
  private final DashboardDistrictNoiseSummaryRepository dashboardDistrictNoiseSummaryRepository;
  private final DashboardDistrictNoiseHourlyRepository dashboardDistrictNoiseHourlyRepository;
  private final DashboardDistrictNoiseYearlyRepository dashboardDistrictNoiseYearlyRepository;
  //private final DashboardDistrictNoiseZoneRepository dashboardDistrictNoiseZoneRepository;
  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate;

  private final BoardEsService boardEsService;

  // 여기에 통계별 Repository 주입

  @Autowired
  private DistrictRepository districtRepository;

  /**
   * 하루 1회 요약 통계(자치구별) 집계 및 저장
   */
  @Transactional
  public void updateNoiseSummary() {
    // 기존 데이터 전체 삭제
    dashboardDistrictNoiseSummaryRepository.deleteAllInBatch();

    // 1. 날짜 범위 계산 (30일 전 ~ 1일 전)
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    LocalDateTime startDate = now.minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime endDate = now.minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(0);

    // 2. 구별 집중/안정 시간 쿼리 실행
    List<Object[]> results = sensorDataRepository.findDistrictPeakAndCalmHourWithAvg(startDate, endDate);

    // 3. 엔티티 리스트 조립
    List<DashboardDistrictNoiseSummary> summaries = new ArrayList<>();
    String batchId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    for (Object[] row : results) {
      String district = (String) row[0];
      String districtCode = districtRepository.findByNameEnOrNameKo(district)
        .map(AutonomousDistrict::getCode)
        .orElse("none");

      log.info("!!!!!!!!district code: {}", districtCode);

      Integer peakHour = row[1] != null ? ((Number) row[1]).intValue() : null;
      BigDecimal peakNoise = row[2] != null ? new BigDecimal(row[2].toString()) : null;
      Integer calmHour = row[3] != null ? ((Number) row[3]).intValue() : null;
      BigDecimal calmNoise = row[4] != null ? new BigDecimal(row[4].toString()) : null;
      BigDecimal avgNoise = row[5] != null ? new BigDecimal(row[5].toString()) : null;


      List<KeywordCount> keywordList = fetchTopKeywords(districtCode, startDate, endDate, 5);

      JsonNode topKeywordsNode = objectMapper.valueToTree(keywordList);

      DashboardDistrictNoiseSummary summary = DashboardDistrictNoiseSummary.builder()
        .batchId(batchId)
        .startDate(startDate.toLocalDate())
        .endDate(endDate.toLocalDate())
        .autonomousDistrictCode(districtCode)
        .peakHour(peakHour)
        .peakNoise(peakNoise)
        .calmHour(calmHour)
        .calmNoise(calmNoise)
        .avgNoise(avgNoise)
        .topKeywords(topKeywordsNode)
        .build();

      summaries.add(summary);
      log.info("summary!! : " + summary);
    }

    log.info("summary row: " + summaries.size());
    dashboardDistrictNoiseSummaryRepository.saveAll(summaries);
  }


  public List<KeywordCount> fetchTopKeywords(String districtCode, LocalDateTime start, LocalDateTime end, int size) {

    Map<String, Long> wordMap = boardEsService.getFrequentWords(districtCode, start, end, size);

    // KeywordCount로 변환
    List<KeywordCount> result = wordMap.entrySet().stream()
      .map(e -> new KeywordCount(e.getKey(), e.getValue().intValue()))
      .collect(Collectors.toList());

    return result;
  }

  /**
   * 시간대별 통계 집계 및 저장
   */
  @Transactional
  public void updateNoiseHourly() {
    dashboardDistrictNoiseHourlyRepository.deleteAllInBatch();

    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    LocalDateTime yesterdayStart = now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime yesterdayEnd = now.minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
    LocalDateTime weekStart = now.minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime monthStart = now.minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime batchEnd = yesterdayEnd;

    List<Object[]> rows = sensorDataRepository.findDistrictNoiseHourly(
      yesterdayStart, yesterdayEnd,
      weekStart, batchEnd,
      monthStart, batchEnd
    );

    String batchId = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    List<DashboardDistrictNoiseHourly> entities = new ArrayList<>();
    for (Object[] row : rows) {
      String district = (String) row[0];
      String districtCode = districtRepository.findByNameEnOrNameKo(district)
        .map(AutonomousDistrict::getCode)
        .orElse("none");

      entities.add(
        DashboardDistrictNoiseHourly.builder()
          .batchId(batchId)
          .startDate(monthStart.toLocalDate())
          .endDate(batchEnd.toLocalDate())
          .autonomousDistrictCode(districtCode)
          .hour(row[1] == null ? null : ((Number) row[1]).intValue())
          .avgDay(row[2] == null ? null : new BigDecimal(row[2].toString()))
          .avgWeek(row[3] == null ? null : new BigDecimal(row[3].toString()))
          .avgMonth(row[4] == null ? null : new BigDecimal(row[4].toString()))
          .build()
      );
    }

    dashboardDistrictNoiseHourlyRepository.saveAll(entities);
    log.info("Inserted hourly stats: {}", entities.size());
  }


  /**
   * 연도별 통계 집계 및 저장
   */
  @Transactional
  public void updateNoiseYearly() {
    List<Object[]> rows = sensorDataRepository.findYearlyNoiseStats();
    String batchId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    int thisYear = LocalDateTime.now().getYear();

    List<DashboardDistrictNoiseYearly> entities = new ArrayList<>();

    for (Object[] row : rows) {
      String regionType = (String) row[0];
      String district = (String) row[1];
      String districtCode = districtRepository.findByNameEnOrNameKo(district)
        .map(AutonomousDistrict::getCode)
        .orElse("seoul-si");

      Integer year = ((Number) row[2]).intValue();
      BigDecimal avgNoise = new BigDecimal(row[3].toString());

      if (year == thisYear) {
        // 올해는 무조건 upsert(갱신/덮어쓰기)
        entities.add(DashboardDistrictNoiseYearly.builder()
          .regionType(regionType)
          .autonomousDistrictCode(districtCode)
          .year(year)
          .avgNoise(avgNoise)
          .batchId(batchId)
          .build());
      } else {
        // 과거년은 이미 있으면 skip
        boolean exists = dashboardDistrictNoiseYearlyRepository.existsByRegionTypeAndAutonomousDistrictCodeAndYear(regionType, districtCode, year);
        if (!exists) {
          entities.add(DashboardDistrictNoiseYearly.builder()
            .regionType(regionType)
            .autonomousDistrictCode(districtCode)
            .year(year)
            .avgNoise(avgNoise)
            .batchId(batchId)
            .build());
        }
      }
    }
    dashboardDistrictNoiseYearlyRepository.saveAll(entities);
  }

  /*
  @Transactional
  public void updateZoneNoise() {
    dashboardDistrictNoiseZoneRepository.deleteAllInBatch();

    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    LocalDate startDate = now.minusDays(30).toLocalDate();
    LocalDate endDate = now.minusDays(1).toLocalDate();
    String batchId = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    List<Object[]> rows = sensorDataRepository.findZoneNoiseStats(batchId, startDate, endDate);

    List<DashboardDistrictNoiseZone> entities = new ArrayList<>();
    for (Object[] row : rows) {
      // 자치구
      String autonomousDistrict =(String) row[3];
      String autonomousDistrictCode = districtRepository.findByNameEnOrNameKo(autonomousDistrict)
        .map(AutonomousDistrict::getCode)
        .orElse(null);

      // 동
      String administrativeDistrict =(String) row[4];
      String administrativeDistrictCode = districtRepository.findByNameEnOrNameKo(administrativeDistrict)
        .map(AutonomousDistrict::getCode)
        .orElse(null);

      entities.add(DashboardDistrictNoiseZone.builder()
        .batchId((String) row[0])
        .startDate(row[1] == null ? null : ((java.sql.Date) row[1]).toLocalDate())
        .endDate(row[2] == null ? null : ((java.sql.Date) row[2]).toLocalDate())
        .autonomousDistrict(autonomousDistrictCode)
        .administrativeDistrict(administrativeDistrictCode)
        .avgNoise(row[5] == null ? null : new BigDecimal(row[5].toString()))
        .build());
    }

    log.info("Inserted zone stats !!!: {}", entities);
    dashboardDistrictNoiseZoneRepository.saveAll(entities);
  }*/


}

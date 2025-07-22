package com.dosion.noisense.module.dashboard.service;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseSummary;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseYearly;
import com.dosion.noisense.module.dashboard.entity.KeywordCount;
import com.dosion.noisense.module.dashboard.repository.*;
import com.dosion.noisense.web.dashboard.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final DashboardDistrictNoiseSummaryRepository summaryRepository;
  private final DashboardDistrictNoiseHourlyRepository hourlyRepository;
  private final DashboardDistrictNoiseYearlyRepository yearlyRepository;
  private final DashboardDistrictNoiseZoneRepository zoneRepository;
  private final NoiseComplaintsRepository noiseComplaintsRepository;
  private final ObjectMapper objectMapper;

  /**
   * 자치구별 최신 소음 요약 데이터를 반환한다.
   * @param district 자치구명 (예: 강남구)
   * @return DTO
   */
  public DistrictNoiseSummaryDto getLatestSummary(String district) {
    DashboardDistrictNoiseSummary entity = summaryRepository
      .findTopByAutonomousDistrictOrderByEndDateDesc(district);

    if (entity == null) {
      throw new IllegalArgumentException("해당 자치구의 소음 데이터가 없습니다: " + district);
    }

    JsonNode topKeywords = entity.getTopKeywords();
    List<KeywordCount> keywordList = Collections.emptyList();
    if (topKeywords != null && topKeywords.isArray()) {
      keywordList = objectMapper.convertValue(
        topKeywords, new TypeReference<List<KeywordCount>>() {});
    }

    return DistrictNoiseSummaryDto.builder()
      .autonomousDistrict(entity.getAutonomousDistrict())
      .startDate(entity.getStartDate())
      .endDate(entity.getEndDate())
      .avgNoise(entity.getAvgNoise())
      .peakHour(entity.getPeakHour())
      .peakNoise(entity.getPeakNoise())
      .calmHour(entity.getCalmHour())
      .calmNoise(entity.getCalmNoise())
      .topKeywords(keywordList)
      .build();
  }

  private List<String> convertKeywordStringToList(String keywords) {
    if (keywords == null || keywords.trim().isEmpty()) {
      return Collections.emptyList();
    }
    return Arrays.stream(keywords.split(",\\s*")).toList();
  }


  public List<DistrictNoiseHourlyDto> getHourlyNoise(String district) {
    return hourlyRepository.findByAutonomousDistrictOrderByHourAsc(district).stream()
      .map(e -> DistrictNoiseHourlyDto.builder()
        .hour(e.getHour())
        .avgDay(e.getAvgDay())
        .avgWeek(e.getAvgWeek())
        .avgMonth(e.getAvgMonth())
        .build())
      .toList();
  }

  public List<DistrictNoiseYearlyDto> getYearlyNoise(String district) {
    int thisYear = Year.now().getValue();
    int fromYear = thisYear - 4;

    String cityKey = "seoul-si";
    String districtKey = district;
    //String districtKey = convertToDbRegionName(district); // 예: "금천구" → "geumcheon-gu"

    List<DashboardDistrictNoiseYearly> list =
      yearlyRepository.findByRegionTypeInAndRegionNameInAndYearBetweenOrderByYearAsc(
        List.of("city", "district"),
        List.of(cityKey, districtKey),
        fromYear,
        thisYear
      );

    return list.stream()
      .collect(Collectors.groupingBy(DashboardDistrictNoiseYearly::getYear))
      .entrySet().stream()
      .sorted(Map.Entry.comparingByKey())
      .map(entry -> {
        Integer year = entry.getKey();
        BigDecimal seoul = null, gu = null;

        for (var e : entry.getValue()) {
          if ("city".equals(e.getRegionType())) seoul = e.getAvgNoise();
          if ("district".equals(e.getRegionType())) gu = e.getAvgNoise();
        }

        return DistrictNoiseYearlyDto.builder()
          .year(year)
          .avgSeoul(seoul)
          .avgDistrict(gu)
          .build();
      })
      .toList();
  }

  public List<DistrictNoiseZoneDto> getZoneNoise(String district) {
    return zoneRepository.findByAutonomousDistrictOrderByAvgNoiseDesc(district).stream()
      .map(e -> DistrictNoiseZoneDto.builder()
        .administrativeDistrict(e.getAdministrativeDistrict())
        .avgNoise(e.getAvgNoise())
        .build())
      .toList();
  }

  public List<DistrictNoiseComplaintsDto> getComplaintsByDistrict(String district) {
    int thisYear = Year.now().getValue();
    int fromYear = thisYear - 4;

    return noiseComplaintsRepository
      .findByAutonomousDistrictAndYearBetweenOrderByYearAsc(district, fromYear, thisYear)
      .stream()
      .map(e -> DistrictNoiseComplaintsDto.builder()
        .year(e.getYear())
        .count(e.getCount())
        .build())
      .toList();
  }

}

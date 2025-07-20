package com.dosion.noisense.module.dashboard.service;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseSummary;
import com.dosion.noisense.module.dashboard.entity.KeywordCount;
import com.dosion.noisense.module.dashboard.repository.DashboardDistrictNoiseSummaryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardSummaryStatService {

  private final DashboardDistrictNoiseSummaryRepository summaryRepository;
  private final ObjectMapper objectMapper;

  // 통계 생성 및 저장 메서드
  public void buildAndSaveSummaryStat(
    LocalDate startDate,
    LocalDate endDate,
    String autonomousDistrict,
    Integer peakHour,
    Integer calmHour,
    List<KeywordCount> topKeywords
  ) {

    JsonNode topKeywordsNode = objectMapper.valueToTree(topKeywords);

    DashboardDistrictNoiseSummary summary = DashboardDistrictNoiseSummary.builder()
      .startDate(startDate)
      .endDate(endDate)
      .autonomousDistrict(autonomousDistrict)
      .peakHour(peakHour)
      .calmHour(calmHour)
      .topKeywords(topKeywordsNode)
      .build();

    summaryRepository.save(summary);
  }
}

package com.dosion.noisense.module.dashboard.service;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseSummary;
import com.dosion.noisense.module.dashboard.repository.DashboardDistrictNoiseSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardSummaryStatService {

  private final DashboardDistrictNoiseSummaryRepository summaryRepository;

  // 통계 생성 및 저장 메서드
  public void buildAndSaveSummaryStat(
    LocalDate startDate,
    LocalDate endDate,
    String autonomousDistrict,
    Integer peakHour,
    Integer calmHour,
    String topKeywords
  ) {
    DashboardDistrictNoiseSummary summary = DashboardDistrictNoiseSummary.builder()
      .startDate(startDate)
      .endDate(endDate)
      .autonomousDistrict(autonomousDistrict)
      .peakHour(peakHour)
      .calmHour(calmHour)
      .topKeywords(topKeywords)
      .build();

    summaryRepository.save(summary);
  }
}

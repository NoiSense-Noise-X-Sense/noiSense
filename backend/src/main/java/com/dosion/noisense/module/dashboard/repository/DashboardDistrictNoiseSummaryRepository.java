package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseSummary;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseSummaryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DashboardDistrictNoiseSummaryRepository extends JpaRepository<DashboardDistrictNoiseSummary, DashboardDistrictNoiseSummaryId> {
  // 특정 자치구의 소음 요약 정보 조회
  //DashboardDistrictNoiseSummary findTopByAutonomousDistrictOrderByEndDateDesc(String autonomousDistrictCode);
  Optional<DashboardDistrictNoiseSummary> findTopByAutonomousDistrictCodeOrderByEndDateDesc(String autonomousDistrictCode);

}

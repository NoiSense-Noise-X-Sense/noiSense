package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseZone;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseZoneId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DashboardDistrictNoiseZoneRepository
  extends JpaRepository<DashboardDistrictNoiseZone, DashboardDistrictNoiseZoneId> {
  // 자치구별 평균 소음 내림차순 조회
  List<DashboardDistrictNoiseZone> findByAutonomousDistrictCodeOrderByAvgNoiseDesc(String autonomousDistrictCode);
}

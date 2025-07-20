package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseZone;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseZoneId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardDistrictNoiseZoneRepository extends JpaRepository<DashboardDistrictNoiseZone, DashboardDistrictNoiseZoneId> {
  void deleteByBatchId(String batchId);  // 배치별 삭제
}

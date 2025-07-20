package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseHourly;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseHourlyId;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DashboardDistrictNoiseHourlyRepository extends JpaRepository<DashboardDistrictNoiseHourly, DashboardDistrictNoiseHourlyId> {

  @Transactional
  @Modifying
  @Query("DELETE FROM DashboardDistrictNoiseHourly d WHERE d.batchId = :batchId")
  void deleteByBatchId(String batchId);

  // 필요시 selectByBatchId, selectByDistrict 등 추가
}

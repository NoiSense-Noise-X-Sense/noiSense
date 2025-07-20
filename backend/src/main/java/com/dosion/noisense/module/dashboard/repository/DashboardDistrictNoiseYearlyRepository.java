package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseYearly;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseYearlyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardDistrictNoiseYearlyRepository extends JpaRepository<DashboardDistrictNoiseYearly, DashboardDistrictNoiseYearlyId> {
  boolean existsByRegionTypeAndRegionNameAndYear(String regionType, String regionName, Integer year);
}

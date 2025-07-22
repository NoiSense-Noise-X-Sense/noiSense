package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseYearly;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseYearlyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DashboardDistrictNoiseYearlyRepository extends JpaRepository<DashboardDistrictNoiseYearly, DashboardDistrictNoiseYearlyId> {
  boolean existsByRegionTypeAndAutonomousDistrictCodeAndYear(
    String regionType, String autonomousDistrictCode, Integer year);

  List<DashboardDistrictNoiseYearly> findByRegionTypeInAndAutonomousDistrictCodeInAndYearBetweenOrderByYearAsc(
    List<String> regionTypes,
    List<String> autonomousDistrictCodes,
    int fromYear,
    int toYear
  );
}


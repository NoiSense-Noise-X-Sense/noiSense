package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseHourly;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseHourlyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DashboardDistrictNoiseHourlyRepository extends JpaRepository<DashboardDistrictNoiseHourly, DashboardDistrictNoiseHourlyId> {

  List<DashboardDistrictNoiseHourly> findByAutonomousDistrictCodeOrderByHourAsc(String autonomousDistrictCode);

}

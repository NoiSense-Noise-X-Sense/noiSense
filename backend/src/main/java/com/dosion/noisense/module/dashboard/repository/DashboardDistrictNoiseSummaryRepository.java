package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseSummary;
import com.dosion.noisense.module.dashboard.entity.DashboardDistrictNoiseSummaryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardDistrictNoiseSummaryRepository extends JpaRepository<DashboardDistrictNoiseSummary, DashboardDistrictNoiseSummaryId> {
}

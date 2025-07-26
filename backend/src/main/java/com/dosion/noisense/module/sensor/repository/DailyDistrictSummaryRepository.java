package com.dosion.noisense.module.sensor.repository;

import com.dosion.noisense.module.sensor.entity.DailyDistrictSummary;
import com.dosion.noisense.module.sensor.entity.DailyDistrictSummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyDistrictSummaryRepository extends JpaRepository<DailyDistrictSummary, DailyDistrictSummaryId>, DailyDistrictSummaryRepositoryCustom {
}

package com.dosion.noisense.module.sensor.repository;

import com.dosion.noisense.module.sensor.entity.HourDistrictSummary;
import com.dosion.noisense.module.sensor.entity.HourDistrictSummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HourDistrictSummaryRepository extends JpaRepository<HourDistrictSummary, HourDistrictSummaryId>, HourDistrictSummaryRepositoryCustom {
}

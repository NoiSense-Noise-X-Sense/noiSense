package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.module.report.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<SensorData, Long>, ReportRepositoryCustom {

}

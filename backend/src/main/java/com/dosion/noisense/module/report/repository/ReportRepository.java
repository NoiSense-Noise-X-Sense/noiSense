package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.module.report.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<SensorData, Long>, ReportRepositoryCustom {

  @Query("SELECT ad.nameKo FROM AdministrativeDistrict ad WHERE ad.code = :code")
  String findAdministrativeByCode(@Param("code") String code);

  @Query("SELECT ad.nameKo FROM AutonomousDistrict ad WHERE ad.code = :code")
  String findAutonomousByCode(@Param("code") String code);

}

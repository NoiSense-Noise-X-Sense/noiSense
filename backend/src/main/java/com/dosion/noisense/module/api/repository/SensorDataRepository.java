package com.dosion.noisense.module.api.repository;

import com.dosion.noisense.module.api.entity.SensorDataApiEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorDataApiEntity, Long>, SensorDataRepositoryCustom {

  // 행정동:측정시간으로
  @Query("SELECT CONCAT(s.administrativeDistrict, ':', s.sensingTime) " +
    "FROM SensorDataApiEntity s " +
    "WHERE s.sensingTime BETWEEN :startTime AND :endTime")
  Set<String> findExistingKeys(@Param("startTime") LocalDateTime minTime, @Param("endTime") LocalDateTime maxTime);

}

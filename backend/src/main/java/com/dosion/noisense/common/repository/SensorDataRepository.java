package com.dosion.noisense.common.repository;

import com.dosion.noisense.common.entity.SensorDataApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorDataApiEntity, Long> {


  // 중복데이터 방지
  @Query("SELECT e.administrativeDistrict || ':' || e.sensingTime FROM SensorDataApiEntity e WHERE e.sensingTime BETWEEN :start AND :end")
  Set<String> findExistingKeys(LocalDateTime start, LocalDateTime end);
}

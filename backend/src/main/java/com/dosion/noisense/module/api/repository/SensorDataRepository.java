package com.dosion.noisense.module.api.repository;

import com.dosion.noisense.module.api.entity.SensorDataApiEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorDataApiEntity, Long>, SensorDataRepositoryCustom {

  // 최신 데이터 불러오기
  @Query("SELECT MAX(s.sensingTime) FROM SensorDataApiEntity s")
  Set<LocalDateTime> findLatestSensingTime();

}

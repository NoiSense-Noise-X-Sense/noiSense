package com.dosion.noisense.module.api.repository;

import com.dosion.noisense.module.api.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long>, SensorDataRepositoryCustom {

  // 최신 데이터 불러오기
  @Query("SELECT MAX(s.sensingTime) FROM SensorData s")
  Set<LocalDateTime> findLatestSensingTime();

  List<Object[]> findDistrictPeakAndCalmHourWithAvg(LocalDateTime startDate, LocalDateTime endDate);

  List<Object[]> findDistrictNoiseHourly(LocalDateTime yesterdayStart, LocalDateTime yesterdayEnd, LocalDateTime weekStart, LocalDateTime batchEnd, LocalDateTime monthStart, LocalDateTime batchEnd1);

  List<Object[]> findYearlyNoiseStats();

  List<Object[]> findZoneNoiseStats(String batchId, LocalDate startDate, LocalDate endDate);
}

package com.dosion.noisense.module.api.repository;

import com.dosion.noisense.module.api.entity.SensorData;

import io.lettuce.core.dynamic.annotation.Param;
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



  // 대시보드 Start

  // 자치구별 소음 집중 시간, 소음 안정 시간
  @Query(value = """
  SELECT
    t.autonomous_district,
    t.peak_hour,
    t.peak_noise,
    t.calm_hour,
    t.calm_noise,
    -- 자치구 한달 평균 소음
    (SELECT AVG(avg_noise)
       FROM sensor_data sd
      WHERE sd.autonomous_district = t.autonomous_district
        AND sd.sensing_time BETWEEN :start AND :end
    ) AS avg_noise
  FROM (
      SELECT
        autonomous_district,
        (ARRAY_AGG(avg_noise_hour ORDER BY avg_by_hour DESC))[1] AS peak_hour,
        (ARRAY_AGG(avg_by_hour ORDER BY avg_by_hour DESC))[1] AS peak_noise,
        (ARRAY_AGG(avg_noise_hour ORDER BY avg_by_hour ASC))[1] AS calm_hour,
        (ARRAY_AGG(avg_by_hour ORDER BY avg_by_hour ASC))[1] AS calm_noise
      FROM (
        SELECT
          autonomous_district,
          EXTRACT(HOUR FROM sensing_time) AS avg_noise_hour,
          AVG(avg_noise) AS avg_by_hour
        FROM sensor_data
        WHERE sensing_time BETWEEN :start AND :end
        GROUP BY autonomous_district, avg_noise_hour
      ) s
      GROUP BY autonomous_district
  ) t
  ORDER BY t.autonomous_district
  """, nativeQuery = true)
  List<Object[]> findDistrictPeakAndCalmHourWithAvg(
    @Param("start") LocalDateTime start,
    @Param("end") LocalDateTime end
  );



  // 시간별 데이터 가져오기
  @Query(value = """
    SELECT
      sd.autonomous_district,
      sd.hour,
      -- 어제 평균
      AVG(CASE WHEN sd.sensing_time BETWEEN :yesterdayStart AND :yesterdayEnd THEN sd.avg_noise END) AS avg_day,
      -- 최근 7일 평균
      AVG(CASE WHEN sd.sensing_time BETWEEN :weekStart AND :weekEnd THEN sd.avg_noise END) AS avg_week,
      -- 최근 30일 평균
      AVG(CASE WHEN sd.sensing_time BETWEEN :monthStart AND :monthEnd THEN sd.avg_noise END) AS avg_month
    FROM (
        SELECT
          autonomous_district,
          EXTRACT(HOUR FROM sensing_time) AS hour,
          avg_noise,
          sensing_time
        FROM sensor_data
        WHERE sensing_time BETWEEN :monthStart AND :yesterdayEnd
    ) sd
    GROUP BY sd.autonomous_district, sd.hour
    ORDER BY sd.autonomous_district, sd.hour
""", nativeQuery = true)
  List<Object[]> findDistrictNoiseHourly(
    @Param("yesterdayStart") LocalDateTime yesterdayStart,
    @Param("yesterdayEnd") LocalDateTime yesterdayEnd,
    @Param("weekStart") LocalDateTime weekStart,
    @Param("weekEnd") LocalDateTime weekEnd,
    @Param("monthStart") LocalDateTime monthStart,
    @Param("monthEnd") LocalDateTime monthEnd
  );

  // 년도별 서울시, 구 데이터
  @Query(value = """
    SELECT 'city' as region_type, 'seoul-si' as region_name,
           EXTRACT(YEAR FROM sensing_time) as year,
           AVG(avg_noise) as avg_noise
      FROM sensor_data
     WHERE autonomous_district IS NOT NULL
     GROUP BY EXTRACT(YEAR FROM sensing_time)
    UNION ALL
    SELECT 'district' as region_type, autonomous_district as region_name,
           EXTRACT(YEAR FROM sensing_time) as year,
           AVG(avg_noise) as avg_noise
      FROM sensor_data
     WHERE autonomous_district IS NOT NULL
     GROUP BY autonomous_district, EXTRACT(YEAR FROM sensing_time)
    """, nativeQuery = true)
  List<Object[]> findYearlyNoiseStats();

  // 동별 평균 소음 데이터
  @Query(value = """
  SELECT
    :batchId AS batch_id,
    CAST(:startDate AS DATE) AS start_date,
    CAST(:endDate AS DATE) AS end_date,
    autonomous_district,
    administrative_district,
    AVG(avg_noise) AS avg_noise
  FROM sensor_data
  WHERE sensing_time BETWEEN :startDate AND :endDate
    AND autonomous_district IS NOT NULL
    AND administrative_district IS NOT NULL
  GROUP BY autonomous_district, administrative_district
  """, nativeQuery = true)
  List<Object[]> findZoneNoiseStats(
    @Param("batchId") String batchId,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
  );


  // 대시보드 End

}

package com.dosion.noisense.module.api.repository;

import com.dosion.noisense.module.api.entity.QSensorData;
import com.dosion.noisense.module.api.entity.QSensorDistrictMapping;
import com.dosion.noisense.module.api.entity.SensorData;
import com.dosion.noisense.module.district.entity.QAdministrativeDistrict;
import com.dosion.noisense.module.district.entity.QAutonomousDistrict;
import com.dosion.noisense.web.report.dto.MaxNoiseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class SensorDataRepositoryImpl implements SensorDataRepositoryCustom {

  private final JdbcTemplate jdbcTemplate;

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public void bulkInsert(List<SensorData> entities) {
    String sql = "INSERT INTO sensor_data" +
      "(sensing_time, region, autonomous_district, administrative_district, " +
      "max_noise, avg_noise, min_noise, max_temp, avg_temp, min_temp, " +
      "max_humi, avg_humi, min_humi, batch_time) " +
      "VALUES (?, CAST(? AS region_type), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        SensorData entity = entities.get(i);
        ps.setObject(1, entity.getSensingTime());

        if (entity.getRegion() != null) {
          // .name() 대신 .getNameEn()을 사용하여 소문자 문자열("residential_area")을 가져옴
          ps.setString(2, entity.getRegion().getNameEn());
        } else {
          // 만약 region 필드가 null일 경우, DB에도 null을 명시적으로 설정
          ps.setNull(2, Types.VARCHAR);
        }

        ps.setString(3, entity.getAutonomousDistrict());
        ps.setString(4, entity.getAdministrativeDistrict());
        ps.setObject(5, entity.getMaxNoise());
        ps.setObject(6, entity.getAvgNoise());
        ps.setObject(7, entity.getMinNoise());
        ps.setObject(8, entity.getMaxTemp());
        ps.setObject(9, entity.getAvgTemp());
        ps.setObject(10, entity.getMinTemp());
        ps.setObject(11, entity.getMaxHumi());
        ps.setObject(12, entity.getAvgHumi());
        ps.setObject(13, entity.getMinHumi());
        ps.setObject(14, LocalDateTime.now());
      }

      @Override
      public int getBatchSize() {
        return entities.size();
      }
    });
  }

  @Override
  public MaxNoiseDto findLoudesDistrict(LocalDate startDate, LocalDate endDate, String autonomousDistrictCode) {

    QSensorData sensorData = QSensorData.sensorData;
    QSensorDistrictMapping sensorDistrictMapping = QSensorDistrictMapping.sensorDistrictMapping;
    QAdministrativeDistrict administrativeDistrict = QAdministrativeDistrict.administrativeDistrict;
    QAutonomousDistrict autonomousDistrict = QAutonomousDistrict.autonomousDistrict;

    StringPath regionPath = null;
    BooleanExpression whereCondition = null;
    if ("all".equalsIgnoreCase(autonomousDistrictCode)) {
      regionPath = autonomousDistrict.nameKo;
      whereCondition = sensorData.autonomousDistrict.ne("Seoul_Grand_Park");
    } else {
      regionPath = administrativeDistrict.nameKo;
      whereCondition = autonomousDistrict.code.eq(autonomousDistrictCode);
    }

    return jpaQueryFactory
      .select(Projections.constructor(MaxNoiseDto.class,
        regionPath,
        sensorData.sensingTime,
        sensorData.maxNoise
      ))
      .from(sensorData)
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .join(administrativeDistrict).on(
        sensorDistrictMapping.adminDistrictCode.eq(administrativeDistrict.code)
      )
      .join(autonomousDistrict).on(
        administrativeDistrict.autonomousDistrict.code.eq(autonomousDistrict.code)
      )
      .where(
        sensorData.sensingTime.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)),
        whereCondition
      )
      .orderBy(sensorData.maxNoise.desc().nullsLast())
      .limit(1)
      .fetchOne();
  }

}

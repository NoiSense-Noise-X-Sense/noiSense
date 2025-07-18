package com.dosion.noisense.module.api.repository;

import com.dosion.noisense.module.api.entity.SensorDataApiEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SensorDataRepositoryImpl implements SensorDataRepositoryCustom {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void bulkInsert(List<SensorDataApiEntity> entities) {
    String sql = "INSERT INTO sensor_data" +
      "(sensing_time, region, autonomous_district, administrative_district, " +
      "max_noise, avg_noise, min_noise, max_temp, avg_temp, min_temp, " +
      "max_humi, avg_humi, min_humi, batch_time) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        SensorDataApiEntity entity = entities.get(i);
        ps.setObject(1, entity.getSensingTime());
        ps.setString(2, entity.getRegion());
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





}

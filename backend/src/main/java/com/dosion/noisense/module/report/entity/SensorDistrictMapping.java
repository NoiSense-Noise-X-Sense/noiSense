package com.dosion.noisense.module.report.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "sensor_district_mapping", schema = "noisense")
public class SensorDistrictMapping {

  @EmbeddedId // 복합 키를 사용함을 명시
  private SensorDistrictMappingId id;

  private String adminDistrictCode;
}

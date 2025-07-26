package com.dosion.noisense.module.sensor.entity;

import com.dosion.noisense.module.sensor.enums.Region;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "hour_district_summary_view", schema = "noisense")
public class HourDistrictSummary {

  @EmbeddedId
  private HourDistrictSummaryId id;

  @Column(name = "autonomous_code")
  private String autonomousCode;

  @Column(name = "autonomous_name_ko")
  private String autonomousNameKo;

  @Column(name = "autonomous_name_en")
  private String autonomousNameEn;

  @Column(name = "administrative_name_ko")
  private String administrativeNameKo;

  @Column(name = "administrative_name_en")
  private String administrativeNameEn;

  @Column(name = "region")
  private Region region;

  @Column(name = "avg_noise")
  private Double avgNoise;

  @Column(name = "data_count")
  private Long dataCount;

}

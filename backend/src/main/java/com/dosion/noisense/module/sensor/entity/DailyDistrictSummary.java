package com.dosion.noisense.module.sensor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "daily_district_summary_view", schema = "noisense")
public class DailyDistrictSummary {

  @EmbeddedId
  private DailyDistrictSummaryId id;

  @Column(name = "autonomous_code")
  private String autonomousCode;

  @Column(name = "autonomous_nameko")
  private String autonomousNameKo;

  @Column(name = "administrative_nameko")
  private String administrativeNameKo;

  @Column(name = "avg_noise")
  private Double avgNoise;

  @Column(name = "max_noise")
  private Double maxNoise;

  @Column(name = "min_noise")
  private Double minNoise;

  @Column(name = "data_count")
  private Long dataCount;

}

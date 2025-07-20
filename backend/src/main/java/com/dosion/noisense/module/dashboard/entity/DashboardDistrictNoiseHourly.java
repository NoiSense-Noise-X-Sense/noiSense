package com.dosion.noisense.module.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dashboard_district_noise_hourly")
@IdClass(DashboardDistrictNoiseHourlyId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DashboardDistrictNoiseHourly implements Serializable {

  @Id
  @Column(name = "batch_id", length = 32, nullable = false)
  @Comment("배치 실행 ID (yyyyMMddHHmmss 등)")
  private String batchId;

  @Id
  @Column(name = "autonomous_district", length = 50, nullable = false)
  @Comment("자치구명")
  private String autonomousDistrict;

  @Id
  @Column(name = "hour", nullable = false)
  @Comment("시 (0~23)")
  private Integer hour;

  @Column(name = "start_date")
  @Comment("통계 시작일")
  private LocalDate startDate;

  @Column(name = "end_date")
  @Comment("통계 종료일")
  private LocalDate endDate;

  @Column(name = "avg_day", precision = 5, scale = 2)
  @Comment("하루(어제) 평균")
  private BigDecimal avgDay;

  @Column(name = "avg_week", precision = 5, scale = 2)
  @Comment("최근 7일 평균")
  private BigDecimal avgWeek;

  @Column(name = "avg_month", precision = 5, scale = 2)
  @Comment("최근 30일 평균")
  private BigDecimal avgMonth;
}

package com.dosion.noisense.module.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@IdClass(DashboardDistrictNoiseZoneId.class)
@Table(name = "dashboard_district_noise_zone")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DashboardDistrictNoiseZone implements Serializable {

  @Id
  @Column(name = "batch_id", length = 32, nullable = false)
  @Comment("배치 실행 ID (yyyyMMddHHmmss 등)")
  private String batchId;

  @Id
  @Column(name = "autonomous_district_code", length = 10, nullable = false)
  @Comment("자치구명")
  private String autonomousDistrictCode;

  @Id
  @Column(name = "administrative_district_code", length = 10, nullable = false)
  @Comment("행정동명")
  private String administrativeDistrictCode;

  @Column(name = "start_date")
  @Comment("통계 시작일")
  private LocalDate startDate;

  @Column(name = "end_date")
  @Comment("통계 종료일")
  private LocalDate endDate;

  @Column(name = "avg_noise", precision = 7, scale = 4)
  @Comment("평균 소음 데시벨")
  private BigDecimal avgNoise;
}

package com.dosion.noisense.module.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@IdClass(DashboardDistrictNoiseYearlyId.class)
@Table(name = "dashboard_district_noise_yearly")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DashboardDistrictNoiseYearly implements Serializable {

  @Id
  @Column(name = "region_type", length = 10, nullable = false)
  @Comment("집계 단위: city, district")
  private String regionType;

  @Id
  @Column(name = "region_name", length = 50, nullable = false)
  @Comment("지역명")
  private String regionName;

  @Id
  @Column(name = "year", nullable = false)
  @Comment("연도")
  private Integer year;

  @Column(name = "batch_id", length = 32, nullable = false)
  @Comment("배치 실행 시각 등")
  private String batchId;

  @Column(name = "avg_noise", precision = 5, scale = 2)
  @Comment("평균 소음 데시벨")
  private BigDecimal avgNoise;
}

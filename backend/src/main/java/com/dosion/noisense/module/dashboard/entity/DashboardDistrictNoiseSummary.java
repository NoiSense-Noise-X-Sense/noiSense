package com.dosion.noisense.module.dashboard.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@IdClass(DashboardDistrictNoiseSummaryId.class)
@Table(name = "dashboard_district_noise_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DashboardDistrictNoiseSummary implements Serializable {

  @Id
  @Column(name = "batch_id", length = 32, nullable = false)
  @Comment("배치 실행 ID (날짜+시간 등)")
  private String batchId;

  @Id
  @Column(name = "autonomous_district_code", length = 10, nullable = false)
  @Comment("자치구명")
  private String autonomousDistrictCode;

  @Column(name = "start_date")
  @Comment("통계 시작일")
  private LocalDate startDate;

  @Column(name = "end_date")
  @Comment("통계 종료일")
  private LocalDate endDate;

  @Column(name = "avg_noise", precision = 5, scale = 2)
  @Comment("한달 평균 소음")
  private BigDecimal avgNoise;

  @Column(name = "peak_hour")
  @Comment("소음 집중 시간대 (시)")
  private Integer peakHour;

  @Column(name = "peak_noise", precision = 5, scale = 2)
  @Comment("소음 집중 데시벨")
  private BigDecimal peakNoise;

  @Column(name = "calm_hour")
  @Comment("소음 안정 시간대 (시)")
  private Integer calmHour;

  @Column(name = "calm_noise", precision = 5, scale = 2)
  @Comment("소음 안정 데시벨")
  private BigDecimal calmNoise;


  @Column(name = "top_keywords", columnDefinition = "jsonb")
  @Comment("상위 민원 키워드 (JSON 배열)")
  @JdbcTypeCode(SqlTypes.JSON)
  private JsonNode topKeywords;
}

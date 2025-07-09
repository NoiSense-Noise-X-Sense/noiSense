package com.dosion.noisense.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "지역별 소음 통계")
public class RegionNoiseDto {

  @Schema(description = "지역구")
  private String region;

  @Schema(description = "이번년도 민원수")
  private int complaintCntThisYear;

  @Schema(description = "한달 평균 소음")
  private float monthlyAvgNoise;

  @Schema(description = "소음 집중 시간")
  private int peakNoiseHour;
  @Schema(description = "소음 집중 시간대의 소음 점수 (dB)")
  private int noiseLvlAtPeakHour;

  @Schema(description = "소음 안정 시간")
  private int quietestHour;
  @Schema(description = "소음 안정 시간대의 소음 점수 (dB)")
  private int noiseLvlAtQuietestHour;

  @Schema(description = "분석 기간")
  private String dateRange;
}

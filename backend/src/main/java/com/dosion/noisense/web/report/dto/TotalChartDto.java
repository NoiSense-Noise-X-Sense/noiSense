package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "모든 차트 정보를 담은 DTO")
public class TotalChartDto {

  @Schema(description = "시간대 별 평균 소음")
  private List<OverallChartDto> overallHourAvgNoiseData;

  @Schema(description = "일 별 평균 소음")
  private List<OverallChartDto> overallDayAvgNoiseData;

  @Schema(description = "시끄럽고 조용한 지역 시간대 별 평균 소음 비교")
  private List<TrendPointChartDto> TrendPointHourAvgNoiseData;

  @Schema(description = "시끄럽고 조용한 지역 요일별 평균 소음 비교")
  private List<TrendPointChartDto> TrendPointDayOfWeekAvgNoiseData;

}

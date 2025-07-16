package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "소음 차트 데이터 DTO / 지역 데이터 있는 꺽은선 그래프")
public class ComparisonChartDto {

  @Schema(description = "x축 / 시간, 일자, 요일 등")
  private String xAxis;

  @Schema(description = "y축 / 평균 소음")
  private Double avgNoise;

  @Schema(description = "지역명")
  private String legion;

}

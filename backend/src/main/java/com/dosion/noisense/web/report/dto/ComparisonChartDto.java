package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "전체 평균 소음 데이터 DTO / 꺽은선그래프용")
public class ComparisonChartDto {

  @Schema(description = "x축 / 시간, 일자, 요일 등")
  private String xAxis;

  @Schema(description = "y축 / 평균 소음")
  private Double avgNoise;

  @Schema(description = "지역명")
  private String name;

}

package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
@Schema(description = "시끄러운 조용한 지역 평균 소음 데이터 DTO / 꺽은선그래프")
public class TrendPointChartDto {

  @Schema(description = "x축 / 시간, 일자, 요일 등")
  private String xAxis;

  @Schema(description = "y축 / key : 지역, value : 평균 소움")
  private Map<String, Double> avgNoiseByRegion;

}

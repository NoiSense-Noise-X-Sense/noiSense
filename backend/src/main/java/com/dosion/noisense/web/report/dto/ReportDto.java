package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "레포트 DTO")
public class ReportDto {

  /*
응답 데이터 목록
  지역 평균 소음 -> Double
  최다 소음 행정동 -> String
  최다 소음 발생 시간대 -> String
  평균 소음 온도 -> String
  시끄러운 지역(Top 3) -> String, Double
  조용한 지역(Top3) -> String, Double
  소음 편차가 큰 지역(Top3) -> String, Double
  --------------------------------------------
  기간 내 시간대 별 평균 소음 그래프 -> x축 인트(시간)
  기간 내 일 별 평균 소음 그래프 ->  x축 인트(일)
  --------------------------------------------
  시간대 별 Top 3/Bottom 3 소음 지역 그래프 ->  x축 인트(시간)
  요일 별 Top 3/Bottom 3 소음 지역 그래프 ->  x축 스트링(요일)
*/
  @Schema(description = "지역 평균 소음")
  private Double avgNoise;

  @Schema(description = "최대 소음 행정동")
  private String maxNoiseRegion;

  @Schema(description = "최대 소음 발생 시간대")
  private String maxNoiseTime;

  @Schema(description = "시끄러운 지역 top3")
  private List<RankDto> topRankDtoList;

  @Schema(description = "조용한 지역 top3")
  private List<RankDto> bottomRankDtoList;

  @Schema(description = "소음 편차 큰 지역 top3")
  private List<DeviationDto> deviationRankDtoList;

  @Schema(description = "그래프DTO")
  private TotalChartDto totalChartDto;

}

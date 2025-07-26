package com.dosion.noisense.module.sensor.repository;

import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.DeviationDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;
import com.dosion.noisense.web.report.dto.RankDto;

import java.time.LocalDate;
import java.util.List;

public interface DailyDistrictSummaryRepositoryCustom {

  /*
해당 지역 평균 소음
 autonomous : 강남구 강동구 등 행정구, 서울시 전국일 경우 all
*/
  Double getAvgNoiseByAutonomousDistrict(LocalDate startDate, LocalDate endDate, String autonomousDistrictCode);


  /*
    해당 지역의 평균 소음 랭크 조회
    autonomous : 강남구 강동구 등 행정구, 서울시 전국일 경우 all
    rankType : 시끄러운 지역과 조용한 지역 구분(소음으로 정렬해서 top은 내림차순, bottom은 오름차순)
      - top : 시끄러운 순위
      - bottom : 조용한 순위
    limit : rank 조회할 수
   */
  List<RankDto> getAvgNoiseRankByRegion(LocalDate startDate, LocalDate endDate, String autonomousDistrict, String rankType, int limit);


  /*
    해당 지역의 편차 랭크 조회
    autonomous : 강남구 강동구 등 행정구, 서울시 전국일 경우 all
    rankType : 편차가 큰지 적은지
      - top : 내림차순
      - bottom : 오름차순
    limit : rank 조회할 수
   */
  List<DeviationDto> getDeviationRankByRegion(LocalDate startDate, LocalDate endDate, String autonomousDistrict, String rankType, int limit);


  /*
    해당 지역의 일별 평균 소음 데이터 조회 (막대그래프)
    autonomous : 강남구 강동구 등 행정구, 서울시 전국일 경우 all
   */
  List<OverallChartDto> getOverallDailyAvgData(LocalDate startDate, LocalDate endDate, String autonomousCode);


  /*
    지역 비교 평균 소음 데이터 조회 (꺽은선그래프)
    type : hour, dayOfMonth, dayOfWeek, month, year / X축 기준이 되는 값
    autonomous : 강남구 강동구 등 행정구, 서울시 전국일 경우 all
    trendPointRegionList : autonomous가 all 이면 행정구 목록 -> 행정구끼리 비교
                                all이 아니면 행정동 목록 -> 행정구의 행정동 끼리 비교
  */
  List<ComparisonChartDto> getTrendPointDayOfWeekAvgData(LocalDate startDate, LocalDate endDate, List<String> trendPointRegionList, String autonomousCode);

  /*
    평균소음과 지역 정보 데이터 조회
    행정구, 행정동이 전체면 all
   */
  // List<AvgNoiseRegionDto> findAverageNoiseByRegion(LocalDateTime startDate, LocalDateTime endDate, String autonomousDistrictCode, String administrativeDistrictCode, List<String> regionList);


}

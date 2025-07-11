package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;

import java.time.LocalDateTime;
import java.util.List;

// queryDsl 사용할 메서드
public interface ReportRepositoryCustom {

  /*
    해당 지역의 평균 소음 데이터 조회 (막대그래프)
    type : hour, dayOfMonth, dayOfWeek, month, year 의 X축 기준이 되는 값
    autonomous : 강남구 강동구 등 행정구, 서울시 전국일 경우 null
   */
  List<OverallChartDto> getOverallAvgData(String type, LocalDateTime startDate, LocalDateTime endDate, String autonomous);



  /*
    지역 비교 평균 소음 데이터 조회 (꺽은선그래프)
    type : hour, dayOfMonth, dayOfWeek, month, year 의 X축 기준이 되는 값
    autonomous : null or 행정구
    trendPointRegionList : autonomous가 null 이면 행정구 목록 -> 행정구끼리 비교
                                null이 아니면 행정동 목록 -> 행정구의 행정동 끼리 비교
  */
  List<ComparisonChartDto> getTrendPointAvgData(String type, LocalDateTime startDate, LocalDateTime endDate, List<String> trendPointRegionList, String autonomous);

}

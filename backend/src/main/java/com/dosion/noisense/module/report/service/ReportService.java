package com.dosion.noisense.module.report.service;

import com.dosion.noisense.module.report.repository.ReportRepository;
import com.dosion.noisense.web.report.dto.OverallChartDto;
import com.dosion.noisense.web.report.dto.TrendPointChartDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;


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


  public String getReport(String startDate, String endDate, String autonomousDistrict){
    log.info("getReport");
/*
    // 데이터 목록들 요청

    // 시끄러운, 조용한 지역 top3 데이터 요청
    // 결과값에 따라서 구 또는 동 add
    List<String> autonomousDistrictList = new ArrayList<>(); // 필터할 구
    List<String> administrativeDistrictList = new ArrayList<>(); // 필터할 동
    // 시끄러운, 조용한 지역 top3 차트 데이터 요청
    // 검색값이 전체면 administrativeDistrictList은 null
    getChartData(startDate, endDate, autonomousDistrictList, administrativeDistrictList);

    // 기간 내 평균 소음 그래프 요청
    getChartData(startDate, endDate, new ArrayList<>(List.of(autonomousDistrict)), null);



*/

    return startDate + endDate + autonomousDistrict;
  }

  // 차트 데이터 가져오기
  private void getChartData(String startDate, String endDate, String autonomousDistrict){
    // type : hourly, daily, dayOfWeek
    // between startDate and endDate
/*
    String autonomous = autonomousDistrict.equals("all") ? null : autonomousDistrict;


    // 1. 시간대 별 평균 소음
    List<OverallChartDto> overallHourlyAvgNoiseData = reportRepository.getOverallAvgData("hourly", startDate, endDate, autonomous);
    // 2. 일 별 평균 소음
    List<OverallChartDto> overallDailyAvgNoiseData = reportRepository.getOverallAvgData("daily", startDate, endDate, autonomous);
    // autonomous 가 null 이면 행정구 조건 없이 데이터를 가져온다

    List<String> TrendPointRegion = null; // 시끄러운곳 행정구 또는 행정동 리스트 -> 메서드 변수값으로 받던지, 메서드로 가져옴
    // 3. 시간대별
    List<TrendPointChartDto> TrendPointHourlyAvgNoiseData = reportRepository.getTrendPointAvgData("hourly", startDate, endDate, TrendPointRegion, autonomous);
    // 4. 요일별
    List<TrendPointChartDto> TrendPointDayOfWeekAvgNoiseData = reportRepository.getTrendPointAvgData("DayOfWeek", startDate, endDate, TrendPointRegion, autonomous);
    // autonomous 가 null 이면 TrendPointRegion은 행정구로 조건을 걸고
    // null이 아니면 autonomous를 행정구 조건, TrendPointRegion 은 행정동 조건
*/
    // 데이터 가공 작업
    log.info("getChartData");

  }








}

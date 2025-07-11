package com.dosion.noisense.module.report.service;

import com.dosion.noisense.module.report.repository.ReportRepository;
import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;
import com.dosion.noisense.web.report.dto.TotalChartDto;
import com.dosion.noisense.web.report.dto.TrendPointChartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  // test 과정에서 TotalChartDto로 설정함. 수정 필요
  public TotalChartDto getReport(LocalDateTime startDate, LocalDateTime endDate, String autonomousDistrict){
    log.info("getReport");

    // 데이터 목록들 요청

    // 시끄러운, 조용한 지역 top3 데이터 요청
    // 결과값에 따라서 구 또는 동 add
    // 메서드가 없어서 임의로 넣음
    List<String> trendPointRegionList = new ArrayList<>(); // 필터할 구
    trendPointRegionList.add("강남구");
    trendPointRegionList.add("강동구");

    // 차트 데이터 요청
    TotalChartDto totalChartDto = getChartData(startDate, endDate, trendPointRegionList, autonomousDistrict);

    return totalChartDto;
  }


  // 차트 데이터 가져오기
  private TotalChartDto getChartData(LocalDateTime startDate, LocalDateTime endDate, List<String> trendPointRegionList, String autonomousDistrict){

    String autonomous = autonomousDistrict.equals("all") ? null : autonomousDistrict;

    // 1. 시간대 별 평균 소음
    List<OverallChartDto> overallHourAvgNoiseData = reportRepository.getOverallAvgData("hour", startDate, endDate, autonomous);
    // 2. 일 별 평균 소음
    List<OverallChartDto> overallDayAvgNoiseData = reportRepository.getOverallAvgData("dayOfMonth", startDate, endDate, autonomous);
    // autonomous 가 null 이면 행정구 조건 없이 데이터를 가져온다

    // 시끄럽고 조용한 지역 비교
    // 3. 시간대별
    List<ComparisonChartDto> TrendPointHourAvgNoiseData = reportRepository.getTrendPointAvgData("hour", startDate, endDate, trendPointRegionList, autonomous);
    // 4. 요일별
    List<ComparisonChartDto> TrendPointDayOfWeekAvgNoiseData = reportRepository.getTrendPointAvgData("dayOfWeek", startDate, endDate, trendPointRegionList, autonomous);

    return TotalChartDto.builder()
      .overallHourAvgNoiseData(overallHourAvgNoiseData)
      .overallDayAvgNoiseData(overallDayAvgNoiseData)
      .TrendPointHourAvgNoiseData(transformToTrendPoint(TrendPointHourAvgNoiseData))
      .TrendPointDayOfWeekAvgNoiseData(transformToTrendPoint(TrendPointDayOfWeekAvgNoiseData))
      .build();
  }


  // DTO 변환해주는 작업
  private List<TrendPointChartDto> transformToTrendPoint(List<ComparisonChartDto> comparisonChartDtoList){

    List<TrendPointChartDto> result = new ArrayList<>();

    // X축 기준으로 그룹화
    Map<String, List<ComparisonChartDto>> groupByXAxis = new HashMap<>();

    for (ComparisonChartDto dto : comparisonChartDtoList) {
      String xAxis = dto.getXAxis();
      List<ComparisonChartDto> list = groupByXAxis.getOrDefault(xAxis, new ArrayList<>());
      list.add(dto);
      groupByXAxis.put(xAxis, list);
    }

    for (Map.Entry<String, List<ComparisonChartDto>> entry : groupByXAxis.entrySet()) {

      Map<String, Double> avgNoiseByRegion = new HashMap<>();
      for (ComparisonChartDto dto : entry.getValue()) {
        avgNoiseByRegion.put(dto.getName(), dto.getAvgNoise());
      }

      result.add(new TrendPointChartDto(entry.getKey(), avgNoiseByRegion));
    }

    return result;
  }

}

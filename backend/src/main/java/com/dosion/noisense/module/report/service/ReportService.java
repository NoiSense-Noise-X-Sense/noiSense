package com.dosion.noisense.module.report.service;

import com.dosion.noisense.module.report.repository.ReportRepository;
import com.dosion.noisense.web.report.dto.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.StringPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.dosion.noisense.module.report.entity.QSensorData.sensorData;

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

  public ReportDto getReport(LocalDateTime startDate, LocalDateTime endDate, String autonomousDistrict) {
    log.info("getReport");

    // 데이터 목록들 요청

    // 지역 평균 소음
    Double avgNoise = reportRepository.getAvgNoiseByAutonomousDistrict(startDate, endDate, autonomousDistrict);

    // 최다 소음 행정동
    // 최다 소음 발생 시간대
    Tuple maxDataByAutonomousDistrict = reportRepository.getMaxDataByAutonomousDistrict(startDate, endDate, autonomousDistrict);
    String maxNoiseRegion = null;
    String maxNoiseTime = null;
    if(maxDataByAutonomousDistrict != null){
      StringPath region = autonomousDistrict.equals("all") ? sensorData.autonomousDistrict : sensorData.administrativeDistrict;
      maxNoiseRegion = maxDataByAutonomousDistrict.get(region);
      maxNoiseTime = getMaxTimeSlot(maxDataByAutonomousDistrict.get(sensorData.sensingTime.hour()));
    }

    /*
    시끄러운 지역(Top 3) -> String, Double
    조용한 지역(Top3) -> String, Double
    소음 편차가 큰 지역(Top3) -> String, Double
    */
    List<RankDto> topRankDtoList = reportRepository.getAvgNoiseRankByRegion(startDate, endDate, autonomousDistrict, "top", 3);
    List<RankDto> bottomRankDtoList = reportRepository.getAvgNoiseRankByRegion(startDate, endDate, autonomousDistrict, "bottom", 3);
    List<DeviationDto> deviationRankDtoList = reportRepository.getDeviationRankByRegion(startDate, endDate, autonomousDistrict, "top", 3);


    // 차트 데이터 요청
    Set<String> trendPointRegionList = new HashSet<>();
    for (RankDto rankDto : topRankDtoList) {
      trendPointRegionList.add(rankDto.getRegion());
    }
    for (RankDto rankDto : bottomRankDtoList) {
      trendPointRegionList.add(rankDto.getRegion());
    }

    TotalChartDto totalChartDto = getChartData(startDate, endDate, new ArrayList<>(trendPointRegionList), autonomousDistrict);

    return ReportDto.builder()
      .avgNoise(avgNoise)
      .maxNoiseRegion(maxNoiseRegion)
      .maxNoiseTime(maxNoiseTime)
      .topRankDtoList(topRankDtoList)
      .bottomRankDtoList(bottomRankDtoList)
      .deviationRankDtoList(deviationRankDtoList)
      .totalChartDto(totalChartDto)
      .build();
  }


  // 차트 데이터 가져오기
  private TotalChartDto getChartData(LocalDateTime startDate, LocalDateTime endDate, List<String> trendPointRegionList, String autonomousDistrict) {

    //레포지토리 넘기기 전에 행정구 및 행정동 영어로 변환 필요
   // String autonomous = autonomousDistrict.equals("all") ? null : autonomousDistrict;

    // 1. 시간대 별 평균 소음
    List<OverallChartDto> overallHourAvgNoiseData = reportRepository.getOverallAvgData("hour", startDate, endDate, autonomousDistrict);
    // 2. 일 별 평균 소음
    List<OverallChartDto> overallDayAvgNoiseData = reportRepository.getOverallAvgData("dayOfMonth", startDate, endDate, autonomousDistrict);
    // autonomous 가 null 이면 행정구 조건 없이 데이터를 가져온다

    // 시끄럽고 조용한 지역 비교
    // 3. 시간대별
    List<ComparisonChartDto> TrendPointHourAvgNoiseData = reportRepository.getTrendPointAvgData("hour", startDate, endDate, trendPointRegionList, autonomousDistrict);
    // 4. 요일별
    List<ComparisonChartDto> TrendPointDayOfWeekAvgNoiseData = reportRepository.getTrendPointAvgData("dayOfWeek", startDate, endDate, trendPointRegionList, autonomousDistrict);

    return TotalChartDto.builder()
      .overallHourAvgNoiseData(overallHourAvgNoiseData)
      .overallDayAvgNoiseData(overallDayAvgNoiseData)
      .TrendPointHourAvgNoiseData(transformToTrendPoint(TrendPointHourAvgNoiseData))
      .TrendPointDayOfWeekAvgNoiseData(transformToTrendPoint(TrendPointDayOfWeekAvgNoiseData))
      .build();
  }


  // ComparsionChartDto로 가져온 데이터를 X축 기준으로 묶어서 변환
  private List<TrendPointChartDto> transformToTrendPoint(List<ComparisonChartDto> comparisonChartDtoList) {

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

  // 최대 소음 발생 시간대
  private String getMaxTimeSlot(Integer hour) {
    if (hour < 0 || hour > 23) {
      throw new IllegalArgumentException("Hour must be between 0 and 23");
    }
    // 3시간 단위로 시간대의 시작 시간을 계산
    // 예: hour가 7, 8이면 -> (7 or 8 / 3) * 3 = 2 * 3 = 6
    //     hour가 14이면 -> (14 / 3) * 3 = 4 * 3 = 12
    int startHour = (hour / 3) * 3;
    int endHour = startHour + 3;

    // String.format을 사용해 "06:00 ~ 09:00" 같은 형식으로 만듦
    return String.format("%02d:00 ~ %02d:00", startHour, endHour);
  }

}

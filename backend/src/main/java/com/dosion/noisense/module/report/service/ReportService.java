package com.dosion.noisense.module.report.service;

import com.dosion.noisense.common.util.PerceivedNoiseCalculator;
import com.dosion.noisense.module.api.repository.SensorDataRepository;
import com.dosion.noisense.module.district.repository.DistrictRepository;
import com.dosion.noisense.web.report.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

  private final SensorDataRepository sensorDataRepository;

  private final DistrictRepository districtRepository;

  private final PerceivedNoiseCalculator perceivedNoiseCalculator;


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

  public ReportDto getReport(LocalDate startDate, LocalDate endDate, String autonomousDistrictCode) {
    log.info("getReport");
    log.info("startDate : {}, endDate : {}, autonomousDistrictCode : {}", startDate, endDate, autonomousDistrictCode);

    // startDate endDate 유효성 검사
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다. 시작일 : " + startDate + ", 종료일 : " + endDate);
    }

    // 지역 평균 소음
    Double avgNoise = sensorDataRepository.getAvgNoiseByAutonomousDistrict(startDate, endDate, autonomousDistrictCode);

    // 최다 소음 정보(지역과 시간)
    MaxNoiseDto maxNoiseDto = sensorDataRepository.findLoudesDistrict(startDate, endDate, autonomousDistrictCode);

    // 체감 소음
    //Double perceivedNoise = avgNoise;
    String autonomousDistrictKor = "all".equals(autonomousDistrictCode) ? "all" : districtRepository.findAutonomousNameKoByCode(autonomousDistrictCode);
    // log.info("체감 소음에 보낼 행정구 : {}", autonomousDistrictKor);
    Double perceivedNoise = perceivedNoiseCalculator.calcPerceivedNoise(avgNoise, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), autonomousDistrictKor, "all");

    /* 랭킹 데이터
    시끄러운 지역(Top 3) -> String, Double
    조용한 지역(Top3) -> String, Double
    소음 편차가 큰 지역(Top3) -> String, Double
    */
    List<RankDto> topRankDtoList = sensorDataRepository.getAvgNoiseRankByRegion(startDate, endDate, autonomousDistrictCode, "top", 3);
    List<RankDto> bottomRankDtoList = sensorDataRepository.getAvgNoiseRankByRegion(startDate, endDate, autonomousDistrictCode, "bottom", 3);
    List<DeviationDto> deviationRankDtoList = sensorDataRepository.getDeviationRankByRegion(startDate, endDate, autonomousDistrictCode, "top", 3);

    /*
    log.info("avgNoise : {}", avgNoise);
    log.info("maxNoiseDto : {}", maxNoiseDto.toString());
    log.info("perceivedNoise : {}", perceivedNoise);

    String logString = "";
    for (RankDto dto : topRankDtoList) {
      logString += "[ " + dto.toString() + " ] ";
    }
    log.info(logString);

    logString = "";
    for (RankDto dto : bottomRankDtoList) {
      logString += "[ " + dto.toString() + " ] ";
    }
    log.info(logString);

    logString = "";
    for (DeviationDto dto : deviationRankDtoList) {
      logString += "[ " + dto.toString() + " ] ";
    }
    log.info(logString);
   */

    // 데이터 유효성 검증
    if (avgNoise == null ||
      maxNoiseDto.getRegion() == null ||
      maxNoiseDto.getMaxNoiseTime() == null ||
      maxNoiseDto.getMaxNoise() == null) {
      throw new NullPointerException("데이터 부족");
    }

    // top3, bottom3 그래프 요청할 지역들을 담은 set
    Set<String> trendPointRegionList = new HashSet<>();
    for (RankDto rankDto : topRankDtoList) {
      trendPointRegionList.add(rankDto.getRegion());
    }
    for (RankDto rankDto : bottomRankDtoList) {
      trendPointRegionList.add(rankDto.getRegion());
    }
    // log.info("trendPointRegionList : {}", trendPointRegionList);

    // 차트 데이터 요청
    TotalChartDto totalChartDto = getChartData(startDate, endDate, new ArrayList<>(trendPointRegionList), autonomousDistrictCode);

    return ReportDto.builder()
      .avgNoise(avgNoise)
      .maxNoiseRegion(maxNoiseDto.getRegion())
      .maxNoiseTime(maxNoiseDto.getMaxNoiseTime())
      .perceivedNoise(perceivedNoise)
      .topRankDtoList(topRankDtoList)
      .bottomRankDtoList(bottomRankDtoList)
      .deviationRankDtoList(deviationRankDtoList)
      .totalChartDto(totalChartDto)
      .build();
  }

  // 차트 데이터 가져오기
  private TotalChartDto getChartData(LocalDate startDate, LocalDate endDate, List<String> trendPointRegionList, String autonomousDistrictCode) {
    log.info("getChartData");
    // 1. 시간대 별 평균 소음
    List<OverallChartDto> overallHourAvgNoiseData = sensorDataRepository.getOverallAvgData("hour", startDate, endDate, autonomousDistrictCode);
    // 2. 일 별 평균 소음
    List<OverallChartDto> overallDayAvgNoiseData = sensorDataRepository.getOverallAvgData("dayOfMonth", startDate, endDate, autonomousDistrictCode);
    // 3. 시간대별 지역 비교
    List<ComparisonChartDto> trendPointHourAvgNoiseData = sensorDataRepository.getTrendPointAvgData("hour", startDate, endDate, trendPointRegionList, autonomousDistrictCode);
    // 4. 요일별 지역 비교
    List<ComparisonChartDto> trendPointDayOfWeekAvgNoiseData = sensorDataRepository.getTrendPointAvgData("dayOfWeek", startDate, endDate, trendPointRegionList, autonomousDistrictCode);

    return TotalChartDto.builder()
      .overallHourAvgNoiseData(overallHourAvgNoiseData)
      .overallDayAvgNoiseData(overallDayAvgNoiseData)
      .TrendPointHourAvgNoiseData(transformToTrendPoint(trendPointHourAvgNoiseData))
      .TrendPointDayOfWeekAvgNoiseData(transformToTrendPoint(trendPointDayOfWeekAvgNoiseData))
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
        avgNoiseByRegion.put(dto.getLegion(), dto.getAvgNoise());
      }
      result.add(new TrendPointChartDto(entry.getKey(), avgNoiseByRegion));
    }

    return result;
  }

  // 최대 소음 발생 시간대
  /*
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
  */

}

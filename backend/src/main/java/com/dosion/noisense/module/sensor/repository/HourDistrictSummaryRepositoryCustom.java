package com.dosion.noisense.module.sensor.repository;

import com.dosion.noisense.web.report.dto.AvgNoiseRegionDto;
import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HourDistrictSummaryRepositoryCustom {


  List<OverallChartDto> getOverallHourAvgData(LocalDate startDate, LocalDate endDate, String autonomousCode);

  List<ComparisonChartDto> getTrendPointHourAvgData(LocalDate startDate, LocalDate endDate, List<String> trendPointRegionList, String autonomousCode);

  List<AvgNoiseRegionDto> findAverageNoiseByRegion(LocalDateTime startDate, LocalDateTime endDate, String autonomousCode, String administrativeCode, List<String> regionList);
}

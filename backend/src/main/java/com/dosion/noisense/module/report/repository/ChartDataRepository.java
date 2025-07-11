package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;
import com.dosion.noisense.web.report.dto.TrendPointChartDto;

import java.util.List;

public interface ChartDataRepository {

  List<OverallChartDto> getOverallAvgData(String type, String startDate, String endDate, String autonomous);

  List<ComparisonChartDto> getTrendPointAvgData(String type, String startDate, String endDate, List<String> trendPointRegion, String autonomous);

}

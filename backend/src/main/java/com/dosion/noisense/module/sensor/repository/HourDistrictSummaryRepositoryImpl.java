package com.dosion.noisense.module.sensor.repository;

import com.dosion.noisense.module.sensor.entity.QHourDistrictSummary;
import com.dosion.noisense.web.report.dto.AvgNoiseRegionDto;
import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class HourDistrictSummaryRepositoryImpl implements HourDistrictSummaryRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<OverallChartDto> getOverallHourAvgData(LocalDate startDate, LocalDate endDate, String autonomousCode) {

    QHourDistrictSummary view = QHourDistrictSummary.hourDistrictSummary;

    NumberExpression<Integer> hourE = view.id.sensingHour.hour();

    return jpaQueryFactory
      .select(Projections.constructor(
        OverallChartDto.class,
        hourE.stringValue(),
        getWeightedAvgNoise(view)
      ))
      .from(view)
      .where(
        betweenDate(view, startDate, endDate),
        eqAutonomousCode(view, autonomousCode)
      )
      .groupBy(hourE)
      .orderBy(hourE.asc())
      .fetch();
  }


  @Override
  public List<ComparisonChartDto> getTrendPointHourAvgData(
    LocalDate startDate, LocalDate endDate, List<String> trendPointRegionList, String autonomousCode) {

    QHourDistrictSummary view = QHourDistrictSummary.hourDistrictSummary;

    NumberExpression<Integer> hourE = view.id.sensingHour.hour();

    StringPath regionPath = null;
    BooleanExpression whereCondition = null;
    if ("all".equalsIgnoreCase(autonomousCode)) {
      regionPath = view.autonomousNameKo;
      whereCondition = view.autonomousNameKo.in(trendPointRegionList);
    } else {
      regionPath = view.administrativeNameKo;
      whereCondition = view.autonomousCode.eq(autonomousCode).and(view.administrativeNameKo.in(trendPointRegionList));
    }

    return jpaQueryFactory
      .select(Projections.constructor(
        ComparisonChartDto.class,
        hourE.stringValue(),
        getWeightedAvgNoise(view),
        regionPath
      ))
      .from(view)
      .where(
        betweenDate(view, startDate, endDate),
        whereCondition
      )
      .groupBy(hourE, regionPath)
      .orderBy(hourE.asc(), regionPath.asc())
      .fetch();
  }


  @Override
  public List<AvgNoiseRegionDto> findAverageNoiseByRegion(LocalDateTime startDate, LocalDateTime endDate, String autonomousCode, String administrativeCode, List<String> regionList) {

    QHourDistrictSummary view = QHourDistrictSummary.hourDistrictSummary;

    return jpaQueryFactory
      .select(Projections.constructor(AvgNoiseRegionDto.class,
        getWeightedAvgNoise(view),
        view.autonomousCode,
        view.autonomousNameEn,
        view.autonomousNameKo,
        view.id.administrativeCode,
        view.administrativeNameEn,
        view.administrativeNameKo
      ))
      .from(view)
      .where(
        view.id.sensingHour.between(startDate, endDate),
        eqAuAdCode(view, autonomousCode, administrativeCode),
        view.region.stringValue().lower().in(regionList)
      )
      .groupBy(
        view.autonomousCode,
        view.autonomousNameEn,
        view.autonomousNameKo,
        view.id.administrativeCode,
        view.administrativeNameEn,
        view.administrativeNameKo
      )
      .fetch();
  }


  private BooleanExpression betweenDate(QHourDistrictSummary view, LocalDate startDate, LocalDate endDate) {
    return view.id.sensingHour.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
  }

  private BooleanExpression eqAutonomousCode(QHourDistrictSummary view, String autonomousCode) {
    if (autonomousCode == null || "all".equalsIgnoreCase(autonomousCode)) {
      return null;
    }
    return view.autonomousCode.eq(autonomousCode);
  }

  private NumberExpression<Double> getWeightedAvgNoise(QHourDistrictSummary view) {

    NumberExpression<Double> weightedSum = view.avgNoise.multiply(view.dataCount).sum();

    NumberExpression<Long> totalCount = view.dataCount.sum();

    return weightedSum.divide(totalCount);
  }

  private BooleanExpression eqAuAdCode(QHourDistrictSummary view, String autonomousCode, String administrativeCode) {
    if (autonomousCode == null || "all".equalsIgnoreCase(autonomousCode)) {
      return null;
    } else if (administrativeCode == null || "all".equalsIgnoreCase(administrativeCode)) {
      return view.autonomousCode.eq(autonomousCode);
    } else {
      return view.autonomousCode.eq(autonomousCode).and(view.id.administrativeCode.eq(administrativeCode));
    }
  }

}

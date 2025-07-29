package com.dosion.noisense.module.sensor.repository;

import com.dosion.noisense.module.sensor.entity.QDailyDistrictSummary;
import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.DeviationDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;
import com.dosion.noisense.web.report.dto.RankDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class DailyDistrictSummaryRepositoryImpl implements DailyDistrictSummaryRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Double getAvgNoiseByAutonomousDistrict(LocalDate startDate, LocalDate endDate, String autonomousCode) {

    QDailyDistrictSummary view = QDailyDistrictSummary.dailyDistrictSummary;

    NumberExpression<Double> weightedAvgNoise = getWeightedAvgNoise(view);

    return jpaQueryFactory
      .select(weightedAvgNoise)
      .from(view)
      .where(
        betweenDate(view, startDate, endDate),
        eqAutonomousCode(view, autonomousCode)
      )
      .fetchOne();
  }

  @Override
  public List<RankDto> getAvgNoiseRankByRegion(LocalDate startDate, LocalDate endDate, String autonomousCode, String rankType, int limit) {

    QDailyDistrictSummary view = QDailyDistrictSummary.dailyDistrictSummary;

    NumberExpression<Double> weightedAvgNoise = getWeightedAvgNoise(view);

    RegionQueryParts queryParts = getRegionQueryParts(view, autonomousCode);

    OrderSpecifier<?> order = "top".equals(rankType) ? weightedAvgNoise.desc() : weightedAvgNoise.asc();

    return jpaQueryFactory
      .select(Projections.constructor(
        RankDto.class,
        queryParts.district,
        weightedAvgNoise
      ))
      .from(view)
      .where(
        betweenDate(view, startDate, endDate),
        queryParts.whereCondition
      )
      .groupBy(queryParts.district)
      .orderBy(order.nullsLast())
      .limit(limit)
      .fetch();
  }

  @Override
  public List<DeviationDto> getDeviationRankByRegion(LocalDate startDate, LocalDate endDate, String autonomousCode, String rankType, int limit) {

    QDailyDistrictSummary view = QDailyDistrictSummary.dailyDistrictSummary;

    RegionQueryParts queryParts = getRegionQueryParts(view, autonomousCode);

    DeviationParts deviationParts = getWeightedDeviation(view);

    OrderSpecifier<?> order = "top".equals(rankType) ? deviationParts.deviation.desc() : deviationParts.deviation.asc();

    return jpaQueryFactory
      .select(Projections.constructor(
        DeviationDto.class,
        queryParts.district,
        deviationParts.avgNoise,
        deviationParts.maxNoise,
        deviationParts.minNoise,
        deviationParts.deviation
      ))
      .from(view)
      .where(
        betweenDate(view, startDate, endDate),
        queryParts.whereCondition
      )
      .groupBy(queryParts.district)
      .orderBy(order.nullsLast())
      .limit(limit)
      .fetch();
  }

  @Override
  public List<OverallChartDto> getOverallDailyAvgData(LocalDate startDate, LocalDate endDate, String autonomousCode) {

    QDailyDistrictSummary view = QDailyDistrictSummary.dailyDistrictSummary;

    NumberExpression<Integer> dayOfMonthE = view.id.summaryDate.dayOfMonth();

    return jpaQueryFactory
      .select(Projections.constructor(
        OverallChartDto.class,
        dayOfMonthE.stringValue(),
        getWeightedAvgNoise(view)
      ))
      .from(view)
      .where(
        betweenDate(view, startDate, endDate),
        eqAutonomousCode(view, autonomousCode)
      )
      .groupBy(dayOfMonthE)
      .orderBy(dayOfMonthE.asc())
      .fetch();
  }

  @Override
  public List<ComparisonChartDto> getTrendPointDayOfWeekAvgData(
    LocalDate startDate, LocalDate endDate, List<String> trendPointRegionList, String autonomousCode) {

    QDailyDistrictSummary view = QDailyDistrictSummary.dailyDistrictSummary;

    StringExpression dayOfWeekE = Expressions.stringTemplate("TO_CHAR({0}, 'D')", view.id.summaryDate);

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
        dayOfWeekE,
        getWeightedAvgNoise(view),
        regionPath
      ))
      .from(view)
      .where(
        betweenDate(view, startDate, endDate),
        whereCondition
      )
      .groupBy(dayOfWeekE, regionPath)
      .orderBy(dayOfWeekE.asc(), regionPath.asc())
      .fetch();
  }


  private BooleanExpression betweenDate(QDailyDistrictSummary view, LocalDate startDate, LocalDate endDate) {
    return view.id.summaryDate.between(startDate, endDate);
  }

  private RegionQueryParts getRegionQueryParts(QDailyDistrictSummary view, String autonomousCode) {
    StringPath regionPath = null;
    BooleanExpression whereCondition = null;

    if ("all".equalsIgnoreCase(autonomousCode)) {
      regionPath = view.autonomousNameKo;
    } else {
      regionPath = view.administrativeNameKo;
      whereCondition = view.autonomousCode.eq(autonomousCode);
    }
    return new RegionQueryParts(regionPath, whereCondition);
  }

  private NumberExpression<Double> getWeightedAvgNoise(QDailyDistrictSummary view) {

    NumberExpression<Double> weightedSum = view.avgNoise.multiply(view.dataCount).sum();

    NumberExpression<Long> totalCount = view.dataCount.sum();

    return weightedSum.divide(totalCount);
  }

  private DeviationParts getWeightedDeviation(QDailyDistrictSummary view) {

    NumberExpression<Long> totalCount = view.dataCount.sum();

    NumberExpression<Double> weightedAvg = view.avgNoise.multiply(view.dataCount).sum().divide(totalCount);

    NumberExpression<Double> weightedMax = view.maxNoise.multiply(view.dataCount).sum().divide(totalCount);

    NumberExpression<Double> weightedMin = view.minNoise.multiply(view.dataCount).sum().divide(totalCount);

    NumberExpression<Double> deviation = weightedMax.subtract(weightedMin);

    return new DeviationParts(weightedAvg, weightedMax, weightedMin, deviation);

  }

  private BooleanExpression eqAutonomousCode(QDailyDistrictSummary view, String autonomousCode) {
    if (autonomousCode == null || "all".equalsIgnoreCase(autonomousCode)) {
      return null;
    }
    return view.autonomousCode.eq(autonomousCode);
  }

  private record RegionQueryParts(StringPath district, BooleanExpression whereCondition) {
  }

  private record DeviationParts(
    NumberExpression<Double> avgNoise,
    NumberExpression<Double> maxNoise,
    NumberExpression<Double> minNoise,
    NumberExpression<Double> deviation
  ) {
  }


}

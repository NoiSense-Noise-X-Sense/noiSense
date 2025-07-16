package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.DeviationDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;
import com.dosion.noisense.web.report.dto.RankDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.dosion.noisense.module.report.entity.QSensorData.sensorData;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Double getAvgNoiseByAutonomousDistrict(LocalDate startDate, LocalDate endDate, String autonomousDistrict) {
    return jpaQueryFactory
      .select(sensorData.avgNoise.avg())
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate)
        , eqAutonomous(autonomousDistrict)
      )
      .fetchOne();
  }


  @Override
  public Tuple getMaxDataByAutonomousDistrict(LocalDate startDate, LocalDate endDate, String autonomousDistrict) {

    StringPath region;
    BooleanBuilder builder = new BooleanBuilder(betweenDate(startDate, endDate));
    if (autonomousDistrict.equals("all")) {
      region = sensorData.autonomousDistrict;
      builder.and(sensorData.autonomousDistrict.ne("Seoul_Grand_Park"));
    } else {
      region = sensorData.administrativeDistrict;
      builder.and(sensorData.autonomousDistrict.eq(autonomousDistrict));
    }

    JPQLQuery<Double> subQuery = JPAExpressions
      .select(sensorData.maxNoise.max())
      .from(sensorData)
      .where(builder);

    return jpaQueryFactory
      .select(
        region
        , sensorData.sensingTime.hour()
        , sensorData.maxNoise
      )
      .from(sensorData)
      .where(
        builder
        , sensorData.maxNoise.eq(subQuery)
      )
      .orderBy(sensorData.sensingTime.desc())
      .limit(1)
      .fetchOne();
  }


  @Override
  public List<RankDto> getAvgNoiseRankByRegion(LocalDate startDate, LocalDate endDate, String autonomousDistrict, String rankType, int limit) {

    StringPath region;
    BooleanExpression excludeParkCondition = null;
    if (autonomousDistrict.equals("all")) {
      region = sensorData.autonomousDistrict;
      excludeParkCondition = sensorData.autonomousDistrict.ne("Seoul_Grand_Park");
    } else {
      region = sensorData.administrativeDistrict;
    }

    OrderSpecifier<?> order = "top".equals(rankType) ? sensorData.avgNoise.avg().desc() : sensorData.avgNoise.avg().asc();

    return jpaQueryFactory
      .select(Projections.constructor(
          RankDto.class
          , region
          , sensorData.avgNoise.avg()
        )
      )
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate)
        , eqAutonomous(autonomousDistrict)
        , excludeParkCondition
      )
      .groupBy(region)
      .orderBy(order.nullsLast())
      .limit(limit)
      .fetch();
  }

  @Override
  public List<DeviationDto> getDeviationRankByRegion(LocalDate startDate, LocalDate endDate, String autonomousDistrict, String rankType, int limit) {

    StringPath region;
    BooleanExpression excludeParkCondition = null;
    if (autonomousDistrict.equals("all")) {
      region = sensorData.autonomousDistrict;
      excludeParkCondition = sensorData.autonomousDistrict.ne("Seoul_Grand_Park");
    } else {
      region = sensorData.administrativeDistrict;
    }

    NumberExpression<Double> deviation = sensorData.maxNoise.max().subtract(sensorData.minNoise.min());

    OrderSpecifier<?> order = "top".equals(rankType) ? deviation.desc() : deviation.asc();

    return jpaQueryFactory
      .select(Projections.constructor(
          DeviationDto.class
          , region
          , sensorData.avgNoise.avg()
          , sensorData.maxNoise.max()
          , sensorData.minNoise.min()
          , deviation
        )
      )
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate)
        , eqAutonomous(autonomousDistrict)
        , excludeParkCondition
      )
      .groupBy(region)
      .orderBy(order.nullsLast())
      .limit(limit)
      .fetch();
  }

  @Override
  public List<OverallChartDto> getOverallAvgData(String type, LocalDate startDate, LocalDate endDate, String autonomousDistrict) {

    StringExpression xAxisE = getXAxisExpression(type);

    return jpaQueryFactory
      .select(Projections.constructor(
        OverallChartDto.class
        , xAxisE
        , sensorData.avgNoise.avg()
      ))
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate)
        , eqAutonomous(autonomousDistrict)
      )
      .groupBy(xAxisE)
      .orderBy(xAxisE.asc())
      .fetch();
  }

  @Override
  public List<ComparisonChartDto> getTrendPointAvgData(
    String type, LocalDate startDate, LocalDate endDate, List<String> trendPointRegionList, String autonomousDistrict) {

    StringExpression xAxisE = getXAxisExpression(type);

    StringPath region = autonomousDistrict.equals("all") ? sensorData.autonomousDistrict : sensorData.administrativeDistrict;

    return jpaQueryFactory
      .select(Projections.constructor(
        ComparisonChartDto.class
        , xAxisE
        , sensorData.avgNoise.avg()
        , region
      ))
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate)
        , eqTrendPointRegion(trendPointRegionList, autonomousDistrict)
      )
      .groupBy(xAxisE, region)
      .orderBy(xAxisE.asc(), region.asc())
      .fetch();
  }


  // between SQL쿼리
  // BETWEEN startDate AND endDate
  private BooleanExpression betweenDate(LocalDate startDate, LocalDate endDate) {
    return sensorData.sensingTime.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
  }

  // autonomous 값이 all이면 null을 반환
  // null이 아니면 Equals  SQL쿼리 반환
  private BooleanExpression eqAutonomous(String autonomous) {
    return autonomous.equals("all") ? null : sensorData.autonomousDistrict.eq(autonomous);
  }

  // autonomous 값이 all 이면 trendPointRegionList는 행정구 조건 SQL쿼리
  // all 이 아니면 trendPointRegionList는 행정동 조건 SQL쿼리
  private BooleanExpression eqTrendPointRegion(List<String> trendPointRegionList, String autonomous) {

    if (autonomous.equals("all")) {
      return sensorData.autonomousDistrict.in(trendPointRegionList);
    } else {
      return sensorData.autonomousDistrict.eq(autonomous).and(sensorData.administrativeDistrict.in(trendPointRegionList));
    }
  }


  // type에 따라 시간 그룹화
  private StringExpression getXAxisExpression(String type) {
    switch (type) {
      case "hour":
        return Expressions.stringTemplate("TO_CHAR({0}, 'HH24')", sensorData.sensingTime);
      case "dayOfMonth":
        return Expressions.stringTemplate("TO_CHAR({0}, 'DD')", sensorData.sensingTime);
      case "dayOfWeek":
        // 일요일:0, 월요일:1, ..., 토요일:6
        return Expressions.stringTemplate("TO_CHAR({0}, 'D')", sensorData.sensingTime);
      case "month":
        return Expressions.stringTemplate("TO_CHAR({0}, 'MM')", sensorData.sensingTime);
      case "year":
        return Expressions.stringTemplate("TO_CHAR({0}, 'YYYY')", sensorData.sensingTime);
      default:
        throw new IllegalArgumentException("Invalid chart XAxis type : " + type);
    }
  }

}

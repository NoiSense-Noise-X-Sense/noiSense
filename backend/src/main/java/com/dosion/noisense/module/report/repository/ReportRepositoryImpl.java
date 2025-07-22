package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.module.sensor.enums.Region;
import com.dosion.noisense.web.report.dto.*;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.dosion.noisense.module.district.entity.QAdministrativeDistrict.administrativeDistrict;
import static com.dosion.noisense.module.district.entity.QAutonomousDistrict.autonomousDistrict;
import static com.dosion.noisense.module.report.entity.QSensorData.sensorData;
import static com.dosion.noisense.module.report.entity.QSensorDistrictMapping.sensorDistrictMapping;

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
        betweenDate(startDate, endDate),
        eqAutonomous(autonomousDistrict)
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
        region,
        sensorData.sensingTime,
        sensorData.maxNoise
      )
      .from(sensorData)
      .where(
        builder,
        sensorData.maxNoise.eq(subQuery)
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
          RankDto.class,
          region,
          sensorData.avgNoise.avg()
        )
      )
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate),
        eqAutonomous(autonomousDistrict),
        excludeParkCondition
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
          DeviationDto.class,
          region,
          sensorData.avgNoise.avg(),
          sensorData.maxNoise.max(),
          sensorData.minNoise.min(),
          deviation
        )
      )
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate),
        eqAutonomous(autonomousDistrict),
        excludeParkCondition
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
        OverallChartDto.class,
        xAxisE,
        sensorData.avgNoise.avg()
      ))
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate),
        eqAutonomous(autonomousDistrict)
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
        ComparisonChartDto.class,
        xAxisE,
        sensorData.avgNoise.avg(),
        region
      ))
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate),
        eqTrendPointRegion(trendPointRegionList, autonomousDistrict)
      )
      .groupBy(xAxisE, region)
      .orderBy(xAxisE.asc(), region.asc())
      .fetch();
  }

  @Override
  public List<AvgNoiseRegionDto> findAverageNoiseByRegion(LocalDateTime startDate, LocalDateTime endDate, String autonomousDistrictEng, String administrativeDistrictEng, List<Region> regionList) {

    List<String> regionAsString = regionList.stream()
      .map( e -> e.getNameEn())
      .collect(Collectors.toList());

    return jpaQueryFactory
      .select(Projections.constructor(AvgNoiseRegionDto.class,
        sensorData.avgNoise.avg(),
        autonomousDistrict.code,
        autonomousDistrict.nameEn,
        autonomousDistrict.nameKo,
        administrativeDistrict.code,
        administrativeDistrict.nameEn,
        administrativeDistrict.nameKo
      ))
      .from(sensorData)
      // 1. SensorData -> Mapping
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .join(administrativeDistrict).on(
        sensorDistrictMapping.adminDistrictCode.eq(administrativeDistrict.code)
      )
      // 3. AdministrativeDistrict -> AutonomousDistrict (코드로 조인)
      .join(autonomousDistrict).on(
        administrativeDistrict.autonomousDistrict.code.eq(autonomousDistrict.code)
      )
      .where(
        // --- JOIN 조건들을 WHERE 절로 이동 ---
//        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn),
//        sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn),
//        sensorDistrictMapping.adminDistrictCode.eq(administrativeDistrict.code),
//        administrativeDistrict.autonomousDistrict.eq(autonomousDistrict),

        sensorData.sensingTime.between(startDate, endDate),
//        eqAutonomousEng(autonomousDistrictEng),
//        eqAdministrativeEng(administrativeDistrictEng),
        eqAuAdEng(autonomousDistrictEng, administrativeDistrictEng),
        sensorData.region.stringValue().in(regionAsString)
      )
      .groupBy(
        autonomousDistrict.code,
        autonomousDistrict.nameEn,
        autonomousDistrict.nameKo,
        administrativeDistrict.code,
        administrativeDistrict.nameEn,
        administrativeDistrict.nameKo
      )
      .having(sensorData.avgNoise.avg().isNotNull())
      .fetch();
  }


  // 센서데이터 테이블 조건
  // 두 날짜 사이 조건 쿼리
  private BooleanExpression betweenDate(LocalDate startDate, LocalDate endDate) {
    return sensorData.sensingTime.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
  }

  // autonomous 값이 all이면 null을 반환
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

  // 행정구테이블 조건
  private BooleanExpression eqAutonomousEng(String nameEn) {
    return (nameEn == null || "all".equalsIgnoreCase(nameEn))
      ? null
      : autonomousDistrict.nameEn.eq(nameEn);
  }

  // 행정동테이블 조건
  private BooleanExpression eqAdministrativeEng(String nameEn) {
    return (nameEn == null || "all".equalsIgnoreCase(nameEn))
      ? null
      : administrativeDistrict.nameEn.eq(nameEn);
  }

  private BooleanExpression eqAuAdEng(String autonomousEng, String administrativeEng) {
    if(autonomousEng == null || "all".equalsIgnoreCase(autonomousEng)) {
      return null;
    }else if(administrativeEng == null || "all".equalsIgnoreCase(administrativeEng)) {
      return autonomousDistrict.nameEn.eq(autonomousEng);
    }else{
      return autonomousDistrict.nameEn.eq(autonomousEng).and(administrativeDistrict.nameEn.eq(administrativeEng));
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
        // 일요일:1, 월요일:2, ..., 토요일:7
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

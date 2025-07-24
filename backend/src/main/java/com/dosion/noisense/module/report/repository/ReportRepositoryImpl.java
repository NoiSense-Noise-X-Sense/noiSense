package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.web.report.dto.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.dosion.noisense.module.district.entity.QAdministrativeDistrict.administrativeDistrict;
import static com.dosion.noisense.module.district.entity.QAutonomousDistrict.autonomousDistrict;
import static com.dosion.noisense.module.report.entity.QSensorData.sensorData;
import static com.dosion.noisense.module.report.entity.QSensorDistrictMapping.sensorDistrictMapping;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Double getAvgNoiseByAutonomousDistrict(LocalDate startDate, LocalDate endDate, String autonomousDistrictCode) {

    return jpaQueryFactory
      .select(sensorData.avgNoise.avg())
      .from(sensorData)
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .where(
        betweenDate(startDate, endDate),
        likeAutonomousDistrictCode(autonomousDistrictCode)
      )
      .fetchOne();
  }

  @Override
  public MaxNoiseDto findLoudesDistrict(LocalDate startDate, LocalDate endDate, String autonomousDistrictCode) {

    RegionQueryParts queryParts = getRegionQueryParts(autonomousDistrictCode);

    return jpaQueryFactory
      .select(Projections.constructor(MaxNoiseDto.class,
        queryParts.district,
        sensorData.sensingTime,
        sensorData.maxNoise
      ))
      .from(sensorData)
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .join(administrativeDistrict).on(
        sensorDistrictMapping.adminDistrictCode.eq(administrativeDistrict.code)
      )
      .join(autonomousDistrict).on(
        administrativeDistrict.autonomousDistrict.code.eq(autonomousDistrict.code)
      )
      .where(
        betweenDate(startDate, endDate),
        queryParts.whereCondition
      )
      .orderBy(sensorData.maxNoise.desc().nullsLast())
      .limit(1)
      .fetchOne();
  }

  @Override
  public List<RankDto> getAvgNoiseRankByRegion(LocalDate startDate, LocalDate endDate, String autonomousDistrictCode, String rankType, int limit) {

    RegionQueryParts queryParts = getRegionQueryParts(autonomousDistrictCode);

    OrderSpecifier<?> order = "top".equals(rankType) ? sensorData.avgNoise.avg().desc() : sensorData.avgNoise.avg().asc();

    return jpaQueryFactory
      .select(Projections.constructor(
        RankDto.class,
        queryParts.district,
        sensorData.avgNoise.avg()
      ))
      .from(sensorData)
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .join(administrativeDistrict).on(
        sensorDistrictMapping.adminDistrictCode.eq(administrativeDistrict.code)
      )
      .join(autonomousDistrict).on(
        administrativeDistrict.autonomousDistrict.code.eq(autonomousDistrict.code)
      )
      .where(
        betweenDate(startDate, endDate),
        queryParts.whereCondition
      )
      .groupBy(queryParts.district)
      .orderBy(order.nullsLast())
      .limit(limit)
      .fetch();
  }

  @Override
  public List<DeviationDto> getDeviationRankByRegion(LocalDate startDate, LocalDate endDate, String autonomousDistrictCode, String rankType, int limit) {

    RegionQueryParts queryParts = getRegionQueryParts(autonomousDistrictCode);

    NumberExpression<Double> deviation = sensorData.maxNoise.avg().subtract(sensorData.minNoise.avg());

    OrderSpecifier<?> order = "top".equals(rankType) ? deviation.desc() : deviation.asc();

    return jpaQueryFactory
      .select(Projections.constructor(
        DeviationDto.class,
        queryParts.district,
        sensorData.avgNoise.avg(),
        sensorData.maxNoise.avg(),
        sensorData.minNoise.avg(),
        deviation
      ))
      .from(sensorData)
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .join(administrativeDistrict).on(
        sensorDistrictMapping.adminDistrictCode.eq(administrativeDistrict.code)
      )
      .join(autonomousDistrict).on(
        administrativeDistrict.autonomousDistrict.code.eq(autonomousDistrict.code)
      )
      .where(
        betweenDate(startDate, endDate),
        queryParts.whereCondition
      )
      .groupBy(queryParts.district)
      .orderBy(order.nullsLast())
      .limit(limit)
      .fetch();
  }

  @Override
  public List<OverallChartDto> getOverallAvgData(String type, LocalDate startDate, LocalDate endDate, String autonomousDistrictCode) {

    StringExpression xAxisE = getXAxisExpression(type);

    return jpaQueryFactory
      .select(Projections.constructor(
        OverallChartDto.class,
        xAxisE,
        sensorData.avgNoise.avg()
      ))
      .from(sensorData)
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .where(
        betweenDate(startDate, endDate),
        likeAutonomousDistrictCode(autonomousDistrictCode)
      )
      .groupBy(xAxisE)
      .orderBy(xAxisE.asc())
      .fetch();
  }

  @Override
  public List<ComparisonChartDto> getTrendPointAvgData(
    String type, LocalDate startDate, LocalDate endDate, List<String> trendPointRegionList, String autonomousDistrictCode) {

    StringExpression xAxisE = getXAxisExpression(type);

    StringPath regionPath = null;
    BooleanExpression whereCondition = null;
    if ("all".equalsIgnoreCase(autonomousDistrictCode)) {
      regionPath = autonomousDistrict.nameKo;
      whereCondition = autonomousDistrict.nameKo.in(trendPointRegionList);
    } else {
      regionPath = administrativeDistrict.nameKo;
      whereCondition = autonomousDistrict.code.eq(autonomousDistrictCode).and(administrativeDistrict.nameKo.in(trendPointRegionList));
    }

    return jpaQueryFactory
      .select(Projections.constructor(
        ComparisonChartDto.class,
        xAxisE,
        sensorData.avgNoise.avg(),
        regionPath
      ))
      .from(sensorData)
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .join(administrativeDistrict).on(
        sensorDistrictMapping.adminDistrictCode.eq(administrativeDistrict.code)
      )
      .join(autonomousDistrict).on(
        administrativeDistrict.autonomousDistrict.code.eq(autonomousDistrict.code)
      )
      .where(
        betweenDate(startDate, endDate),
        whereCondition
      )
      .groupBy(xAxisE, regionPath)
      .orderBy(xAxisE.asc(), regionPath.asc())
      .fetch();
  }

  @Override
  public List<AvgNoiseRegionDto> findAverageNoiseByRegion(LocalDateTime startDate, LocalDateTime endDate, String autonomousDistrictCode, String administrativeDistrictCode, List<String> regionList) {

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
      .join(sensorDistrictMapping).on(
        sensorData.autonomousDistrict.eq(sensorDistrictMapping.id.sensorAutoDistrictEn)
          .and(sensorData.administrativeDistrict.eq(sensorDistrictMapping.id.sensorAdminDistrictEn))
      )
      .join(administrativeDistrict).on(
        sensorDistrictMapping.adminDistrictCode.eq(administrativeDistrict.code)
      )
      .join(autonomousDistrict).on(
        administrativeDistrict.autonomousDistrict.code.eq(autonomousDistrict.code)
      )
      .where(
        sensorData.sensingTime.between(startDate, endDate),
        eqAuAdCode(autonomousDistrictCode, administrativeDistrictCode),
        sensorData.region.stringValue().lower().in(regionList)
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


  private BooleanExpression betweenDate(LocalDate startDate, LocalDate endDate) {
    return sensorData.sensingTime.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
  }

  private RegionQueryParts getRegionQueryParts(String autonomousDistrictCode) {
    StringPath regionPath = null;
    BooleanExpression whereCondition = null;
    if ("all".equalsIgnoreCase(autonomousDistrictCode)) {
      regionPath = autonomousDistrict.nameKo;
      whereCondition = sensorData.autonomousDistrict.ne("Seoul_Grand_Park");
    } else {
      regionPath = administrativeDistrict.nameKo;
      whereCondition = autonomousDistrict.code.eq(autonomousDistrictCode);
    }
    return new RegionQueryParts(regionPath, whereCondition);
  }

  private BooleanExpression likeAutonomousDistrictCode(String autonomousDistrictCode) {
    if (autonomousDistrictCode == null || "all".equalsIgnoreCase(autonomousDistrictCode)) {
      return null;
    }
    return sensorDistrictMapping.adminDistrictCode.like(autonomousDistrictCode + "%");
  }

  private BooleanExpression eqAuAdCode(String autonomousDistrictCode, String administrativeDistrictCode) {
    if (autonomousDistrictCode == null || "all".equalsIgnoreCase(autonomousDistrictCode)) {
      return null;
    } else if (administrativeDistrictCode == null || "all".equalsIgnoreCase(administrativeDistrictCode)) {
      return autonomousDistrict.code.eq(autonomousDistrictCode);
    } else {
      return autonomousDistrict.code.eq(autonomousDistrictCode).and(administrativeDistrict.code.eq(administrativeDistrictCode));
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

  private record RegionQueryParts(StringPath district, BooleanExpression whereCondition) {
  }

/*
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
*/

}

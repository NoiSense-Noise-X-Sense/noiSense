package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.web.report.dto.ComparisonChartDto;
import com.dosion.noisense.web.report.dto.OverallChartDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.dosion.noisense.module.report.entity.QSensorData.sensorData;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom{

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<OverallChartDto> getOverallAvgData(String type, LocalDateTime startDate, LocalDateTime endDate, String autonomous) {

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
        , eqAutonomous(autonomous)
      )
      .groupBy(xAxisE)
      .orderBy(xAxisE.asc())
      .fetch();
  }

  @Override
  public List<ComparisonChartDto> getTrendPointAvgData(
    String type, LocalDateTime startDate, LocalDateTime endDate, List<String> trendPointRegionList, String autonomous) {

    StringExpression xAxisE = getXAxisExpression(type);

    StringPath region = autonomous == null ? sensorData.autonomousDistrict : sensorData.administrativeDistrict;

    return jpaQueryFactory
      .select(Projections.constructor(
        ComparisonChartDto.class
        , xAxisE
        , sensorData.avgNoise.avg()
        ,region
      ))
      .from(sensorData)
      .where(
        betweenDate(startDate, endDate)
        , eqTrendPointRegion(trendPointRegionList, autonomous)
      )
      .groupBy(xAxisE, region)
      .orderBy(xAxisE.asc(), region.asc())
      .fetch();
  }



  // between SQL쿼리
  // BETWEEN startDate AND endDate
  private BooleanExpression betweenDate(LocalDateTime startDate, LocalDateTime endDate){
    return sensorData.sensingTime.between(startDate, endDate);
  }

  // autonomous 값이 null이면 null을 반환
  // null이 아니면 Equals  SQL쿼리 반환
  private BooleanExpression eqAutonomous(String autonomous){
    return autonomous == null ? null : sensorData.autonomousDistrict.eq(autonomous) ;
  }

  // autonomous 값이 null 이면 trendPointRegionList는 행정구 조건 SQL쿼리
  // null 이 아니면 trendPointRegionList는 행정동 조건 SQL쿼리
  private BooleanExpression eqTrendPointRegion(List<String> trendPointRegionList, String autonomous){

    BooleanExpression regionEx = sensorData.autonomousDistrict.in(trendPointRegionList);
    if(autonomous != null){
      return sensorData.autonomousDistrict.eq(autonomous).and(regionEx);
    }
    return regionEx;
  }


  // type에 따라 시간 그룹화
  private StringExpression getXAxisExpression(String type){
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

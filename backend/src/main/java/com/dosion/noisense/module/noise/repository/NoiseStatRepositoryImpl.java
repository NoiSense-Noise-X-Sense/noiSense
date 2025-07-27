package com.dosion.noisense.module.noise.repository;

import com.dosion.noisense.module.noise.projection.NoiseStatDto;
import com.dosion.noisense.module.sensor.entity.QHourDistrictSummary;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoiseStatRepositoryImpl implements NoiseStatRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<NoiseStatDto> findAverageNoiseByAutonomousDistrict(LocalDateTime startDate, LocalDateTime endDate) {
    QHourDistrictSummary h = QHourDistrictSummary.hourDistrictSummary;

    return queryFactory
      .select(Projections.constructor(NoiseStatDto.class,
        h.autonomousCode,
        h.autonomousNameKo,
        h.autonomousNameEn,
        // 자치구는 상위 없음
        null,
        h.avgNoise,
        null // 체감 소음은 mapper에서 계산 예정
      ))
      .from(h)
      .where(h.id.sensingHour.between(startDate, endDate))
      .groupBy(h.autonomousCode, h.autonomousNameKo, h.autonomousNameEn)
      .fetch();
  }

  @Override
  public List<NoiseStatDto> findAverageNoiseByAdministrativeDistrict(LocalDateTime startDate, LocalDateTime endDate, String parentDistrictCode) {
    QHourDistrictSummary h = QHourDistrictSummary.hourDistrictSummary;

    return queryFactory
      .select(Projections.constructor(NoiseStatDto.class,
        h.id.administrativeCode,
        h.administrativeNameKo,
        h.administrativeNameEn,
        h.autonomousCode,
        h.avgNoise,
        null
      ))
      .from(h)
      .where(
        h.id.sensingHour.between(startDate, endDate),
        h.autonomousCode.eq(parentDistrictCode)
      )
      .groupBy(h.id.administrativeCode, h.administrativeNameKo, h.administrativeNameEn, h.autonomousCode)
      .fetch();
  }

  @Override
  public List<NoiseStatDto> findAverageNoiseByAllAdministrativeDistrict(LocalDateTime startDate, LocalDateTime endDate, List<String> regionList) {
    QHourDistrictSummary h = QHourDistrictSummary.hourDistrictSummary;

    return queryFactory
      .select(Projections.constructor(NoiseStatDto.class,
        h.id.administrativeCode,
        h.administrativeNameKo,
        h.administrativeNameEn,
        h.autonomousCode,
        h.avgNoise,
        null
      ))
      .from(h)
      .where(
        h.id.sensingHour.between(startDate, endDate),
        h.region.stringValue().in(regionList)
      )
      .groupBy(h.id.administrativeCode, h.administrativeNameKo, h.administrativeNameEn, h.autonomousCode)
      .fetch();
  }

  @Override
  public List<NoiseStatDto> findAverageNoiseByAllAutonomousDistrict(LocalDateTime startDate, LocalDateTime endDate, List<String> regionList) {
    QHourDistrictSummary h = QHourDistrictSummary.hourDistrictSummary;

    return queryFactory
      .select(Projections.constructor(NoiseStatDto.class,
        h.autonomousCode,
        h.autonomousNameKo,
        h.autonomousNameEn,
        null,
        h.avgNoise,
        null
      ))
      .from(h)
      .where(
        h.id.sensingHour.between(startDate, endDate),
        h.region.stringValue().in(regionList)
      )
      .groupBy(h.autonomousCode, h.autonomousNameKo, h.autonomousNameEn)
      .fetch();
  }
}

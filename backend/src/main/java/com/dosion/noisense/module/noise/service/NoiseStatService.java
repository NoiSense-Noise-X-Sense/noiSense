package com.dosion.noisense.module.noise.service;

import com.dosion.noisense.module.noise.projection.NoiseStatDto;
import com.dosion.noisense.module.noise.repository.NoiseStatRepository;
import com.dosion.noisense.module.board.repository.BoardRepository;
import com.dosion.noisense.web.noise.dto.response.*;
import com.dosion.noisense.module.noise.mapper.NoiseMapper;
import com.dosion.noisense.web.report.dto.EmotionBoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoiseStatService {

  private final NoiseStatRepository noiseStatRepository;
  private final BoardRepository boardRepository;

  public MainMapNoiseResponseDto getMainMapStats(LocalDateTime startDate, LocalDateTime endDate) {
    List<NoiseStatDto> stats = noiseStatRepository.findAverageNoiseByAutonomousDistrict(startDate, endDate);

    List<NoiseStatDto> withPerceived = stats.stream()
      .map(stat -> {
        List<EmotionBoardDto> emotions = boardRepository.findEmotionScoresByCriteria(
          startDate, endDate,
          stat.autonomousDistrictCode(), "all"
        );
        double perceived = calcPerceivedNoise(stat.avgNoise(), emotions);
        return new NoiseStatDto(
          stat.autonomousDistrictCode(),
          stat.autonomousDistrictKor(),
          stat.autonomousDistrictEng(),
          null, null, null,
          stat.avgNoise(),
          perceived
        );
      })
      .collect(Collectors.toList());

    return NoiseMapper.toMainMapResponse(withPerceived);
  }

  public DashboardMapNoiseResponseDto getDashboardMapStats(LocalDateTime startDate, LocalDateTime endDate, String autonomousCode) {
    List<NoiseStatDto> stats = noiseStatRepository.findAverageNoiseByAdministrativeDistrict(startDate, endDate, autonomousCode);

    List<NoiseStatDto> withPerceived = stats.stream()
      .map(stat -> {
        List<EmotionBoardDto> emotions = boardRepository.findEmotionScoresByCriteria(
          startDate, endDate,
          autonomousCode, stat.administrativeDistrictCode()
        );
        double perceived = calcPerceivedNoise(stat.avgNoise(), emotions);
        return new NoiseStatDto(
          stat.autonomousDistrictCode(),
          stat.autonomousDistrictKor(),
          stat.autonomousDistrictEng(),
          stat.administrativeDistrictCode(),
          stat.administrativeDistrictKor(),
          stat.administrativeDistrictEng(),
          stat.avgNoise(),
          perceived
        );
      })
      .collect(Collectors.toList());

    return NoiseMapper.toDashboardMapResponse(withPerceived);
  }

  public FullMapNoiseResponseDto getFullMapStatsAutonomous(LocalDateTime startDate, LocalDateTime endDate, List<String> regionList) {
    List<NoiseStatDto> stats = noiseStatRepository.findAverageNoiseByAllAutonomousDistrict(startDate, endDate, regionList);

    List<NoiseStatDto> withPerceived = stats.stream()
      .map(stat -> {
        List<EmotionBoardDto> emotions = boardRepository.findEmotionScoresByCriteria(
          startDate, endDate,
          stat.autonomousDistrictCode(), "all"
        );
        double perceived = calcPerceivedNoise(stat.avgNoise(), emotions);
        return new NoiseStatDto(
          stat.autonomousDistrictCode(),
          stat.autonomousDistrictKor(),
          stat.autonomousDistrictEng(),
          null, null, null,
          stat.avgNoise(),
          perceived
        );
      })
      .collect(Collectors.toList());

    return NoiseMapper.toFullMapResponse(withPerceived, List.of());
  }

  public FullMapNoiseResponseDto getFullMapStatsAdministrative(LocalDateTime startDate, LocalDateTime endDate, List<String> regionList) {
    List<NoiseStatDto> stats = noiseStatRepository.findAverageNoiseByAllAdministrativeDistrict(startDate, endDate, regionList);

    List<NoiseStatDto> withPerceived = stats.stream()
      .map(stat -> {
        List<EmotionBoardDto> emotions = boardRepository.findEmotionScoresByCriteria(
          startDate, endDate,
          stat.autonomousDistrictCode(), stat.administrativeDistrictCode()
        );
        double perceived = calcPerceivedNoise(stat.avgNoise(), emotions);
        return new NoiseStatDto(
          stat.autonomousDistrictCode(),
          stat.autonomousDistrictKor(),
          stat.autonomousDistrictEng(),
          stat.administrativeDistrictCode(),
          stat.administrativeDistrictKor(),
          stat.administrativeDistrictEng(),
          stat.avgNoise(),
          perceived
        );
      })
      .collect(Collectors.toList());

    return NoiseMapper.toFullMapResponse(List.of(), withPerceived);
  }

  private double calcPerceivedNoise(Double avgNoise, List<EmotionBoardDto> emotions) {
    // 감정 점수 평균 구하기 (예시)
    double emotionScoreAvg = emotions.stream()
      .mapToDouble(e -> e.getEmotionalScore())
      .average()
      .orElse(0.0);

    // 예시 공식: 체감 소음 = 평균소음 + 감정가중치
    return avgNoise + emotionScoreAvg * 0.5;
  }

}

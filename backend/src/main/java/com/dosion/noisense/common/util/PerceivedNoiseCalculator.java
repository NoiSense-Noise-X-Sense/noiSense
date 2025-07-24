package com.dosion.noisense.common.util;

import com.dosion.noisense.module.board.repository.BoardRepository;
import com.dosion.noisense.web.report.dto.EmotionBoardDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class PerceivedNoiseCalculator { // 체감 소음 계산

  private final BoardRepository boardRepository;

  /*
   * =================================================================
   * 체감 소음 계산 공식
   * =================================================================
   * P=D0 +(log10(n+1)×α) + ∑(score i×α×β)
   *
   * - P = 체감 소음
   * - D0: 기본 소음 (avgNoise)
   * - n: 민원 총 개수 (boardList.size())
   * - alpha: 민원 가중치 계수
   * - beta: 시간 가중치 계수
   * - score: 감정계수
   * =================================================================
   */

  /*
   *  avgNoise : 평균소음
   *  startDate : 시작일 -> 시간까지 들어가야하면 LocalDateTime 으로 수정
   *  endDate : 종료일
   *  autonomousDistrict : 행정구
   *  게시판 기능 개발 중이라 그거에 따라 getBoardInfo 수정하여 사용해야 함
   *  행정동까지 필요하면 변수 추가
   * */

  public Double calcPerceivedNoise(double avgNoise, LocalDateTime startDate, LocalDateTime endDate, String autonomousDistrictKor, String administrativeDistrictKor) {
    /*
    log.info("PerceivedNoiseCalculator.calcPerceivedNoise");
    log.info("avgNoise ====" + avgNoise);
    log.info("startDate ====" + startDate);
    log.info("endDate ====" + endDate);
    log.info("autonomousDistrict ====" + autonomousDistrict);
    log.info("administrativeDistrict ====" + administrativeDistrict);
    */

    // 민원 가중치 알파,  추후 값 수정 필요
    final Double ALPHA = 1.2;

    /*
      시간 가중치 베타,  추후 값 수정 필요
        00:00 ~ 06:00 / 1.4
        06:00 ~ 12:00 / 1.0
        12:00 ~ 18:00 / 0.8
        18:00 ~ 24:00 / 1.1
     */
    Map<Integer, Double> timeWeights = new HashMap<>();
    timeWeights.put(0, 1.4);
    timeWeights.put(6, 1.0);
    timeWeights.put(12, 0.8);
    timeWeights.put(18, 1.1);

    // 게시글
    List<EmotionBoardDto> boardList = boardRepository.findEmotionScoresByCriteria(startDate, endDate, autonomousDistrictKor, administrativeDistrictKor);
    // log.info("boardList ====" + boardList.toString());

    // 게시글이 없으면 평균 소음 반환
    if (boardList.isEmpty()) {
      log.info("PerceivedNoise / boardList is empty -> return avgNoise : " + avgNoise);
      return avgNoise;
    }

    Double logValue = Math.log10(boardList.size() + 1) * ALPHA;

    Double emotionScoreByTimeWeight = 0.0;
    for (EmotionBoardDto emo : boardList) {
      Double beta = timeWeights.get((emo.getCreatedDate().getHour() / 6) * 6);
      emotionScoreByTimeWeight += emo.getEmotionalScore() * ALPHA * beta;
    }

    return avgNoise + logValue + emotionScoreByTimeWeight;
  }
}

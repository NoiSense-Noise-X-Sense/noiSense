package com.dosion.noisense.module.board.repository;

import com.dosion.noisense.module.board.entity.Board;
import com.dosion.noisense.web.report.dto.EmotionBoardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT new com.dosion.noisense.web.report.dto.EmotionBoardDto(b.emotionalScore, b.createdDate) " +
            "FROM Board b " +
            "WHERE b.createdDate BETWEEN :startDate AND :endDate " +
            "AND ( " +
            // 1. autonomousDistrict가 'all'인 경우 (모든 데이터 포함)
            "   (:autonomousDistrictKor = 'all') " +
            // 2. OR: autonomousDistrict는 값이 있고, administrativeDistrict가 'all'인 경우 (자치구로만 필터링)
            "   OR (:administrativeDistrictKor = 'all' AND b.autonomousDistrict = :autonomousDistrictKor) " +
            // 3. OR: 두 파라미터 모두 값이 있는 경우 (자치구와 행정동 모두로 필터링)
            "   OR (b.autonomousDistrict = :autonomousDistrictKor AND b.administrativeDistrict = :administrativeDistrictKor) " +
            ")")
    List<EmotionBoardDto> findEmotionScoresByCriteria(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("autonomousDistrictKor") String autonomousDistrictKor,
            @Param("administrativeDistrictKor") String administrativeDistrictKor
    );
}

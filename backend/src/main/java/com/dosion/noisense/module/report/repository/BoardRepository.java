package com.dosion.noisense.module.report.repository;

import com.dosion.noisense.module.report.entity.Board;
import com.dosion.noisense.web.report.dto.EmotionBoardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

  @Query("SELECT b.emotionalScore as emotionalScore, b.createdDate as createdDate " +
    "FROM Board b " +
    "WHERE b.createdDate BETWEEN :startDate AND :endDate " +
    "AND (:autonomousDistrict IS NULL OR b.autonomousDistrict = :autonomousDistrict)" +
    "AND (:administrativeDistrict IS NULL OR b.administrativeDistrict = :administrativeDistrict)")
  List<EmotionBoardDto> findEmotionScoresByCriteria(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("autonomousDistrict") String autonomousDistrict,
    @Param("administrativeDistrict") String administrativeDistrict
  );
}

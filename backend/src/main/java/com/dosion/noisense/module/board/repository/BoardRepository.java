package com.dosion.noisense.module.board.repository;

import com.dosion.noisense.web.board.dto.BoardDto;
import com.dosion.noisense.module.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

  /** 특정 User의 게시글만 조회 **/
  List<Board> findByUserId(Long userId);

  /** 전체 게시글 페이징 조회 **/
  @Query("SELECT new com.dosion.noisense.web.board.dto.BoardDto(" +
    "b.id, b.userId, b.nickname, b.title, b.content, " +
    "b.emotionalScore, b.empathyCount, b.viewCount, " +
    "(SELECT COUNT(c) FROM Comment c WHERE c.board.id = b.id), " +
    "b.autonomousDistrictCode, b.administrativeDistrictCode, " +
    "b.createdDate, b.modifiedDate) " +
    "FROM Board b " +
    "ORDER BY b.createdDate DESC")
  Page<BoardDto> findAllPaging(Pageable pageable);

  /** 특정 User의 게시글 페이징 조회 **/
  @Query("SELECT new com.dosion.noisense.web.board.dto.BoardDto(" +
    "b.id, b.userId, b.nickname, b.title, b.content, " +
    "b.emotionalScore, b.empathyCount, b.viewCount, " +
    "(SELECT COUNT(c) FROM Comment c WHERE c.board.id = b.id), " + // 댓글 수
    "b.autonomousDistrictCode, b.administrativeDistrictCode, " +
    "b.createdDate, b.modifiedDate) " +
    "FROM Board b " +
    "WHERE b.userId = :userId " +
    "ORDER BY b.createdDate DESC")
  Page<BoardDto> findByUserIdPaging(Long userId, Pageable pageable);

  void deleteByUserId(Long userId);


  // 체감소음 계산하기 위한 감정계수 조회
  @Query("SELECT new com.dosion.noisense.web.report.dto.EmotionBoardDto(b.emotionalScore, b.createdDate) " +
    "FROM Board b " +
    "WHERE b.createdDate BETWEEN :startDate AND :endDate " +
    "AND ( " +
    // 1. autonomousDistrict가 'all'인 경우 (모든 데이터 포함)
    "   (:autonomousDistrictCode = 'all') " +
    // 2. OR: autonomousDistrict는 값이 있고, administrativeDistrict가 'all'인 경우 (자치구로만 필터링)
    "   OR (:administrativeDistrictCode = 'all' AND b.autonomousDistrictCode = :autonomousDistrictCode) " +
    // 3. OR: 두 파라미터 모두 값이 있는 경우 (자치구와 행정동 모두로 필터링)
    "   OR (b.autonomousDistrictCode = :autonomousDistrictCode AND b.administrativeDistrictCode = :administrativeDistrictCode) " +
    ")")
  List<EmotionBoardDto> findEmotionScoresByCriteria(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("autonomousDistrictCode") String autonomousDistrictCode,
    @Param("administrativeDistrictCode") String administrativeDistrictCode
  );
}

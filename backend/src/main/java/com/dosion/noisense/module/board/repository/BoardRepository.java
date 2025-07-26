package com.dosion.noisense.module.board.repository;

import com.dosion.noisense.web.board.dto.BoardDto;
import com.dosion.noisense.module.board.entity.Board;
import com.dosion.noisense.module.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
}

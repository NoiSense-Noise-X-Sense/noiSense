package com.dosion.noisense.web.board.controller;

import com.dosion.noisense.web.board.dto.BoardDto;
import com.dosion.noisense.module.board.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Board-Controller", description = "게시판 API")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  /** 게시글 작성 **/
  @PostMapping
  public ResponseEntity<BoardDto> createBoard(@RequestBody BoardDto boardDto) {
    // Security 미구현 상태: userId, nickname을 프론트에서 받아 사용
    BoardDto createdBoard = boardService.createBoard(boardDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
  }

  /** 게시글 상세 조회 **/
  @GetMapping("/{id}")
  public ResponseEntity<BoardDto> getBoardDetail(@PathVariable Long id) {
    BoardDto boardDto = boardService.getBoardById(id);
    return ResponseEntity.ok(boardDto);
  }

  /** 게시글 수정 **/
  @PutMapping("/{id}")
  public ResponseEntity<BoardDto> updateBoard(
    @PathVariable Long id,
    @RequestParam Long userId, // 쿼리 파라미터로 userId 전달
    @RequestBody BoardDto boardDto) {

    BoardDto updatedBoard = boardService.updateBoard(id, userId, boardDto);
    return ResponseEntity.ok(updatedBoard);
  }


  /** 게시글 삭제 **/
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteBoard(
    @PathVariable Long id,
    @RequestParam Long userId // Security 미구현 상태: userId를 쿼리 파라미터로 전달
  ) {
    boardService.deleteBoard(id, userId);
    return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
  }

  /** 게시글 목록 페이징 조회 **/
  @GetMapping
  public ResponseEntity<Page<BoardDto>> getBoards(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    Page<BoardDto> boardPage = boardService.getBoards(page, size);
    return ResponseEntity.ok(boardPage);
  }

  @PostMapping("/{id}/empathy")
  public ResponseEntity<String> toggleEmpathyCount(
    @PathVariable("id") Long boardId,
    @RequestParam("userId") Long userId
  ) {
    boardService.toggleEmpathyCount(boardId, userId);
    return ResponseEntity.ok("공감 상태가 변경되었습니다.");
  }

}

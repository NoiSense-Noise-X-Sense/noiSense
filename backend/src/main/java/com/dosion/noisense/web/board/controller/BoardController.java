package com.dosion.noisense.web.board.controller;

import com.dosion.noisense.common.security.core.CustomUserDetails;
import com.dosion.noisense.web.board.dto.BoardDto;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsDocument;
import com.dosion.noisense.module.board.service.BoardService;
import com.dosion.noisense.module.board.elasticsearch.service.BoardEsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Board-Controller", description = "게시판 API")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;
  private final BoardEsService boardEsService;

  /** 게시글 작성 **/
  @PostMapping
  public ResponseEntity<?> createBoard(
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @RequestBody List<BoardDto> boardDtoList) {

    Long userId = userDetails.getId();
    log.info("[getUserInfo] userId : {}", userId);
    log.info("[getUserInfo] username : {}", userDetails.getUsername());

    String nickname = userDetails.getNickname();

    List<BoardDto> fixedDtoList = boardDtoList.stream()
      .map(dto -> BoardDto.builder()
        .boardId(dto.getBoardId())
        .userId(userId)
        .nickname(nickname)
        .title(dto.getTitle())
        .content(dto.getContent())
        .emotionalScore(dto.getEmotionalScore())
        .empathyCount(dto.getEmpathyCount())
        .viewCount(dto.getViewCount())
        .autonomousDistrict(dto.getAutonomousDistrict())
        .administrativeDistrict(dto.getAdministrativeDistrict())
        .createdDate(dto.getCreatedDate())
        .modifiedDate(dto.getModifiedDate())
        .build())
      .toList();

    fixedDtoList.forEach(boardService::createBoard);
    return ResponseEntity.ok().build();
  }

  /** 통합 검색 **/
  @GetMapping("/search")
  public ResponseEntity<Page<BoardEsDocument>> searchBoards(
    @RequestParam String keyword,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    Page<BoardEsDocument> results = boardEsService.search(keyword, page, size);
    return ResponseEntity.ok(results);
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
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @RequestBody BoardDto boardDto) {

    Long userId = userDetails.getId();
    BoardDto updatedBoard = boardService.updateBoard(id, userId, boardDto);
    return ResponseEntity.ok(updatedBoard);
  }

  /** 게시글 삭제 **/
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteBoard(
    @PathVariable Long id,
    @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getId();
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
    @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getId();
    boardService.toggleEmpathyCount(boardId, userId);
    return ResponseEntity.ok("공감 상태가 변경되었습니다.");
  }


  @Operation(summary = "현재 로그인한 사용자의 게시글 목록 페이징 조회", description = "로그인한 사용자의 게시글 목록을 페이징하여 조회합니다.")
  @GetMapping("/my-board")
  public ResponseEntity<Page<BoardDto>> getMyBoards(
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    Long userId = userDetails.getId();
    log.info("[getMyBoards] userId: {}", userId);
    Page<BoardDto> boardPage = boardService.getBoardsByUserId(userId, page, size);
    return ResponseEntity.ok(boardPage);
  }


}

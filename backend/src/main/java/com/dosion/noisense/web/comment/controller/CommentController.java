package com.dosion.noisense.web.comment.controller;

import com.dosion.noisense.common.security.core.CustomUserDetails;
import com.dosion.noisense.web.comment.dto.CommentDto;
import com.dosion.noisense.module.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment-Controller", description = "댓글 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  /** 댓글 작성 **/
  @PostMapping
  public ResponseEntity<CommentDto> saveComment(
    @AuthenticationPrincipal com.dosion.noisense.common.security.core.CustomUserDetails userDetails,
    @RequestBody CommentDto commentDto
  ) {
    Long userId = userDetails.getId();
    commentDto.setUserId(userId);
    CommentDto savedComment = commentService.saveComment(commentDto);
    return ResponseEntity.ok(savedComment);
  }

  /** 특정 게시글의 댓글 목록 조회 **/
  @GetMapping
  public ResponseEntity<List<CommentDto>> getCommentsByBoardId(@RequestParam Long boardId) {
    List<CommentDto> comments = commentService.findCommentsByBoardId(boardId);
    return ResponseEntity.ok(comments);
  }

  /** 댓글 단건 삭제 **/
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
    commentService.deleteComment(id);
    return ResponseEntity.ok().build();
  }

  /** 댓글 단건 조회 **/
  @GetMapping("/{id}")
  public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
    CommentDto commentDto = commentService.findCommentById(id);
    return ResponseEntity.ok(commentDto);
  }

  /** 현재 로그인한 유저의 댓글 개수 조회 **/
  @Operation(summary = "내 댓글 개수 조회", description = "현재 로그인한 유저가 작성한 댓글의 개수를 조회합니다.")
  @GetMapping("/count/my")
  public ResponseEntity<Long> getMyCommentCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getId();
    long commentCount = commentService.countCommentsByUserId(userId);
    return ResponseEntity.ok(commentCount);
  }

}

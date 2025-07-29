package com.dosion.noisense.module.comment.service;

import com.dosion.noisense.module.board.entity.Board;
import com.dosion.noisense.module.board.repository.BoardRepository;
import com.dosion.noisense.module.comment.entity.Comment;
import com.dosion.noisense.module.comment.repository.CommentRepository;
import com.dosion.noisense.module.user.entity.Users;
import com.dosion.noisense.module.user.repository.UserRepository;
import com.dosion.noisense.web.comment.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final BoardRepository boardRepository;
  private final UserRepository userRepository;

  /** 댓글 저장 **/
  @Transactional
  public CommentDto saveComment(CommentDto commentDto) {

    Board board = boardRepository.findById(commentDto.getBoardId())
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

    Users user = userRepository.findById(commentDto.getUserId())
      .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

    Comment comment = new Comment();
    comment.setContent(commentDto.getContent());
    comment.setNickname(user.getNickname());  // nickname 직접 저장
    comment.setBoard(board);
    comment.setUser(user);

    Comment savedComment = commentRepository.save(comment);
    return toDto(savedComment);
  }

  /** 게시글의 댓글 전체 조회 **/
  @Transactional(readOnly = true)
  public List<CommentDto> findCommentsByBoardId(Long boardId) {
    List<Comment> comments = commentRepository.findByBoardId(boardId);
    return comments.stream()
      .map(this::toDto)
      .collect(Collectors.toList());
  }

  /** 댓글 삭제 **/
  @Transactional
  public void deleteComment(Long id) {
    commentRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public CommentDto findCommentById(Long id) {
    Comment comment = commentRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
    return toDto(comment);
  }

  /** 특정 유저의 댓글 개수 조회 **/
  @Transactional(readOnly = true)
  public long countCommentsByUserId(Long userId) {
    return commentRepository.countByUserId(userId);
  }




  /** Entity → DTO 변환 **/
  public CommentDto toDto(Comment entity) {
    CommentDto dto = new CommentDto();
    dto.setId(entity.getId()); // 수정: getCommentId() → getId()
    dto.setBoardId(entity.getBoard().getId());
    
    // user 정보가 null인지 확인
    if (entity.getUser() != null) {
      dto.setUserId(entity.getUser().getId());
      dto.setNickname(entity.getNickname()); // 저장된 nickname 사용
    } else {
      System.out.println("WARNING: Comment " + entity.getId() + " has null user!");
      dto.setNickname(entity.getNickname()); // 저장된 nickname 사용
    }
    
    dto.setContent(entity.getContent());
    dto.setCreatedDate(entity.getCreatedDate()); // 수정: getCreated_date() → getCreatedDate()
    dto.setUpdatedDate(entity.getModifiedDate()); // 수정: getUpdated_date() → getModifiedDate()
    return dto;
  }

}

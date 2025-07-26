package com.dosion.noisense.module.comment.repository;

import com.dosion.noisense.module.comment.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @EntityGraph(attributePaths = {"user", "board"})
  List<Comment> findByBoardId(Long boardId);
  
  Long countByBoardId(Long boardId);
}

package com.dosion.noisense.module.comment.repository;

import com.dosion.noisense.module.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByBoardId(Long boardId);
}

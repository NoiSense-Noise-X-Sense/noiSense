package com.dosion.noisense.module.board.repository;

import com.dosion.noisense.module.board.entity.BoardEmpathy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardEmpathyRepository extends JpaRepository<BoardEmpathy, Long> {
  Optional<BoardEmpathy> findByBoardIdAndUserId(Long boardId, Long userId);

}

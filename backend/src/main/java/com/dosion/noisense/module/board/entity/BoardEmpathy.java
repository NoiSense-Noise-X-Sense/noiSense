package com.dosion.noisense.module.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board_empathy")
public class BoardEmpathy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "board_empathy_id")
  private Long id;

  @Column(name = "board_id", nullable = false)
  private Long boardId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "created_date")
  private LocalDateTime createdDate;
}

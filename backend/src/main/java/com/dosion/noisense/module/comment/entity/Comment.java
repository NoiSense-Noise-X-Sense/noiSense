package com.dosion.noisense.module.comment.entity;

import com.dosion.noisense.common.entity.BaseEntity;
import com.dosion.noisense.module.board.entity.Board;
import com.dosion.noisense.module.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id")
  private Long id;

  @Column(name = "content", nullable = false, length = 500)
  private String content;

  /**
   * Board 연관관계
   * 게시글 삭제 시 댓글도 함께 삭제되도록 CASCADE 설정
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Board board;

  /**
   * Users 연관관계
   * 유저 삭제 시 댓글도 함께 삭제되도록 CASCADE 설정
   * 필요에 따라 CASCADE 대신 NULL 처리 방식으로 변경 가능
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Users user;

  /**
   * 유저의 닉네임을 편하게 얻기 위한 Getter
   */
  public String getNickname() {
    return user != null ? user.getNickname() : null;
  }
}

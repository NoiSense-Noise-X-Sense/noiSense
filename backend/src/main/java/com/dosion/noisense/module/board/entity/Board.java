package com.dosion.noisense.module.board.entity;

import com.dosion.noisense.module.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board")
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "board_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private Users user; // 연관관계는 권한검증/상세조회용

  @Column(name = "user_id", nullable = false)
  private Long userId; // 작성자 ID를 별도로 저장

  @Column(name = "nickname")
  private String nickname; // 작성자 닉네임을 별도로 저장

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "emotional_score")
  private Long emotionalScore;

  @Builder.Default
  @Column(name = "empathy_count")
  private Long empathyCount = 0L;

  @Builder.Default
  @Column(name = "view_count", nullable = false)
  private Long viewCount = 0L;

  @Column(name = "autonomous_district_code", length = 10, nullable = false)
  @Comment("자치구 코드")
  private String autonomousDistrictCode;

  @Column(name = "administrative_district_code", length = 10, nullable = false)
  @Comment("행정동 코드")
  private String administrativeDistrictCode;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Builder.Default
  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<com.dosion.noisense.module.comment.entity.Comment> comments = new ArrayList<>();
}

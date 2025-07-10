package com.dosion.noisense.module.auth.entity;

import com.dosion.noisense.common.entity.BaseEntity;
import com.dosion.noisense.module.user.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor // 모든 생성자
@NoArgsConstructor // 기본생성자 자동생성
@Getter
@Setter
@Entity
public class Auth extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String tokenType;

  @Column(nullable = false)
  private String accessToken;

  @Column(nullable = false)
  private String refreshToken;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="user_id", nullable = false)
  private Users user;


}

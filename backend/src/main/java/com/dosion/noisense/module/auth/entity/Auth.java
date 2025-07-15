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
import org.hibernate.annotations.Comment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Auth extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Auth_ID")
  @Comment("회원 아이디")
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

  public Auth(Users user, String refreshToken, String accessToken, String tokenType) {
    this.user = user;
    this.refreshToken = refreshToken;
    this.accessToken = accessToken;
    this.tokenType = tokenType;
  }

  // updateAccessToken 메서드 추가
  //토큰값을 업데이트 해주는 메서드
  public void updateAccessToken(String newAccessToken) {
    this.accessToken = newAccessToken;
  }

  // updateRefreshToken 메서드 추가
  public void updateRefreshToken(String newRefreshToken) {
    this.refreshToken = newRefreshToken;
  }


}

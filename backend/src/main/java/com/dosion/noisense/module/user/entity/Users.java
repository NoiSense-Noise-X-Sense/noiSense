package com.dosion.noisense.module.user.entity;

import com.dosion.noisense.common.security.core.Role;
import com.dosion.noisense.module.auth.entity.Auth;
import com.dosion.noisense.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users  extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "USER_ID")
  @Comment("회원 아이디")
  private Long id;

  @Comment("유저명")
  @Column(nullable = false, unique = true)
  private String userNm;

  @Comment("닉네임")
  @Column
  private String nickname;

  @Comment("이메일")
  @Column
  private String email;

  @Comment("자치구")
  @Column
  private String autonomousDistrict;

  @Comment("행정동")
  @Column
  private String administrativeDistrict;

  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Auth auth;

}

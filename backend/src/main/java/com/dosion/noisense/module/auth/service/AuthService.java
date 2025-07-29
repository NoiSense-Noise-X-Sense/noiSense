package com.dosion.noisense.module.auth.service;

import com.dosion.noisense.common.security.core.CustomUserDetails;
import com.dosion.noisense.common.security.core.Role;
import com.dosion.noisense.common.security.jwt.JwtTokenProvider;
import com.dosion.noisense.module.auth.entity.Auth;
import com.dosion.noisense.module.auth.repository.AuthRepository;
import com.dosion.noisense.module.user.entity.Users;
import com.dosion.noisense.module.user.repository.UserRepository;
import com.dosion.noisense.web.user.dto.LoginRequestDto;
import com.dosion.noisense.web.user.dto.LoginResponseDto;
import com.dosion.noisense.web.user.dto.SignUpRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthRepository authRepository;
  private final UserRepository userRepository;
//  private final PasswordEncoder passwordEncoder;

  private final JwtTokenProvider jwtTokenProvider;

  @Value("${jwt.accessTokenExpirationTime}")
  private Long jwtAccessTokenExpirationTime;
  @Value("${jwt.refreshTokenExpirationTime}")
  private Long jwtRefreshTokenExpirationTime;

  // 회원가입
  @Transactional
  public void signUp(SignUpRequestDto dto) {
    if (userRepository.findByUserNm(dto.getUserNm()).isPresent()){
      throw new RuntimeException("User already exists");
    }
    Users user = new Users();
    user.setUserNm(dto.getUserNm());
//    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRole(Role.ROLE_USER); // 일반 사용자로 회원가입

    user.setNickname(dto.getNickname());
    user.setEmail(dto.getEmail());

    /** 저장 */
    userRepository.save(user);


  }


  // 로그인
  public LoginResponseDto login(LoginRequestDto loginRequestDto){
    Users user = userRepository.findByUserNm(loginRequestDto.getUserNm())
      .orElseThrow(()->new RuntimeException("해당 유저를 찾을 수 없습니다."));

  /*  if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())){
      throw new BadCredentialsException("비밀번호가 일치 하지 않습니다.");
      // 시큐리티 로그인 과정에서 비밀번호가 일치하지 않으면 던져주는 예외
    }*/

    // 위 비밀번호가 일치하면 기존 토큰 정보를 비교하고 토큰이 있으면 업데이트, 없으면 새로발급
    String accessToken = jwtTokenProvider.generateToken(
      new UsernamePasswordAuthenticationToken(new CustomUserDetails(user)
        ,""), jwtAccessTokenExpirationTime);

    String refreshToken = jwtTokenProvider.generateToken(
      new UsernamePasswordAuthenticationToken(new CustomUserDetails(user)
        ,""), jwtRefreshTokenExpirationTime);

    // 현재 로그인한 사람이 DB에 있는지 확인하고 있으면 토큰을 DB에 저장하고 로그인처리
    if(authRepository.existsByUser(user)){
      Auth auth = user.getAuth();
      auth.setRefreshToken(refreshToken);
      auth.setAccessToken(accessToken);
      authRepository.save(auth);

      return new LoginResponseDto(auth);
    }


    //=================================================
    // 위에서 DB 사용자 정보가 없으면 아래 새로 생성해서 로그인 처리
    Auth auth = new Auth(user, refreshToken,accessToken,"Bearer");
    authRepository.save(auth);
    return new LoginResponseDto(auth);
  }

  @Transactional
  public String refreshToken (String refreshToken){
    // 리프레시 토큰 유효성 검사
    Auth auth = authRepository.findByRefreshToken(refreshToken).orElseThrow(
      ()-> new IllegalArgumentException("해당 REFRESH_TOKEN 을 찾을 수 없습니다. \n refreshToken  = " +refreshToken));

    // 있으면 인증객체를 만들어서 새로운 토큰 발급
    String newAccessToken = jwtTokenProvider.generateToken(
      new UsernamePasswordAuthenticationToken(
        new CustomUserDetails(auth.getUser()), ""), jwtAccessTokenExpirationTime);

    auth.updateAccessToken(newAccessToken);
    authRepository.save(auth);
    return newAccessToken;
  }


}

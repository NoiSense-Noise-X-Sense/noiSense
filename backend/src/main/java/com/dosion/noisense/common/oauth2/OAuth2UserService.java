package com.dosion.noisense.common.oauth2;

import com.dosion.noisense.common.security.core.CustomUserDetails;
import com.dosion.noisense.common.security.core.Role;
import com.dosion.noisense.common.security.jwt.JwtTokenProvider;
import com.dosion.noisense.module.auth.entity.Auth;
import com.dosion.noisense.module.auth.repository.AuthRepository;
import com.dosion.noisense.module.user.entity.Users;
import com.dosion.noisense.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

  private final AuthRepository authRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${jwt.accessTokenExpirationTime}")
  private Long jwtAccessTokenExpirationTime;
  @Value("${jwt.refreshTokenExpirationTime}")
  private Long jwtRefreshTokenExpirationTime;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getRegistrationId();
    Map<String, Object> attributes = oAuth2User.getAttributes();

    String userNm, nickname, email;

    if ("google".equals(provider)) {
      email = (String) attributes.get("email");
      userNm = email;
      nickname = (String) attributes.get("name");
    } else if ("kakao".equals(provider)) {
      userNm = attributes.get("id").toString() + "@kakao";
      Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
      email = (String) kakaoAccount.get("email");
      Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
      nickname = (String) profile.get("nickname");
    } else {
      email = null;
      userNm = null;
      nickname = null;
    }

    log.info(provider + " 로그인 확인 userNm = " + userNm);
    log.info(provider + " 로그인 확인 email = " + email);
    log.info(provider + " 로그인 확인 nickname = " + nickname);

    Users user = userRepository.findByUserNmOrEmail(userNm, email)
      .orElseGet(() -> {
        log.info("[OAuth2] 신규회원: name={}, nickname={}", userNm, nickname);
        Users newUser = new Users();
        newUser.setUserNm(userNm);
        newUser.setNickname(nickname);
        newUser.setEmail(email);
        newUser.setRole(Role.ROLE_USER);
        return userRepository.save(newUser);
      });

    log.info("찾은 getUserNm =" + user.getUserNm());
    log.info("찾은 getNickname =" + user.getNickname());
    log.info("찾은 getUserid =" + user.getId());
    log.info("찾은 getEmail =" + user.getEmail());

    CustomUserDetails customUserDetails = new CustomUserDetails(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
      customUserDetails,
      Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())));

    String accessToken = jwtTokenProvider.generateToken(authentication,
      jwtAccessTokenExpirationTime);
    String refreshToken = jwtTokenProvider.generateToken(authentication,
      jwtRefreshTokenExpirationTime);

    Map<String, Object> customAttributes = new HashMap<>(attributes);
    customAttributes.put("accessToken", accessToken);
    customAttributes.put("refreshToken", refreshToken);
    customAttributes.put("id", user.getId());
    customAttributes.put("userNm", user.getUserNm());
    customAttributes.put("nickname", user.getNickname());

    Optional<Auth> optionalAuth = authRepository.findByUser(user);
    if (optionalAuth.isPresent()) {
      Auth auth = optionalAuth.get();
      auth.updateAccessToken(accessToken);
      auth.updateRefreshToken(refreshToken);
      authRepository.save(auth);
      user.setAuth(auth);
    } else {
      Auth auth = new Auth(user, refreshToken, accessToken, "Bearer ");
      authRepository.save(auth);
      user.setAuth(auth);
    }

    return new DefaultOAuth2User(
      Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
      customAttributes,
      "id"
    );
  }
}

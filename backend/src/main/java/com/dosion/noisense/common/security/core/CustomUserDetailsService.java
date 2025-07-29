package com.dosion.noisense.common.security.core;

import com.dosion.noisense.module.user.entity.Users;
import com.dosion.noisense.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Users user = userRepository.findByUserNm(username).orElseThrow(() -> new UsernameNotFoundException("해당 회원이 존재하지 않습니다. userNm =>" + username));
    return new CustomUserDetails(user);
  }

  public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
    Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("해당 회원이 존재하지 않습니다. id =>" + userId));
    return new CustomUserDetails(user);
  }

}

package com.dosion.noisense.module.user.service;

import com.dosion.noisense.web.user.dto.UserDto;
import com.dosion.noisense.module.user.entity.Users;
import com.dosion.noisense.module.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public void add(UserDto userDto) {
    if (userRepository.findByUserNm(userDto.getUserNm()).isPresent()){
      throw new RuntimeException("User already exists");
    }
    Users user = new Users();
    user.setUserNm(userDto.getUserNm());
    user.setNickname(userDto.getNickname());
    user.setEmail(userDto.getEmail());
    user.setAutonomous_district(userDto.getAutonomousDistrict());
    user.setAdministrative_district(userDto.getAdministrativeDistrict());

    /** 저장 */
     userRepository.save(user);
  }
}

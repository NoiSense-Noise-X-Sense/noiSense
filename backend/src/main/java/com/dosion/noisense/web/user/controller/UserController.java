package com.dosion.noisense.web.user.controller;

import com.dosion.noisense.web.user.dto.UserDto;
import com.dosion.noisense.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user") //변경
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> getMyInfo(@Valid @RequestBody UserDto userDto) {
    userService.add(userDto);
//    return ResponseEntity.ok();
    return null;
  }

}

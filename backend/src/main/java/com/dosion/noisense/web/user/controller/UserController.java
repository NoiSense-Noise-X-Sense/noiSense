package com.dosion.noisense.web.user.controller;

import com.dosion.noisense.web.user.dto.UserDto;
import com.dosion.noisense.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user") //변경
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(summary = "회원 생성")
  @PostMapping
  public ResponseEntity<UserDto> add(@RequestBody UserDto userDto) {
    log.info("[controller ] userNm : " + userDto.getUserNm());
    log.info("[controller ] nickname : " + userDto.getNickname());
    userService.add(userDto);
//    return ResponseEntity.ok();
    return null;
  }

}

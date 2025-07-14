package com.dosion.noisense.web.user.controller;

import com.dosion.noisense.common.security.core.CustomUserDetails;
import com.dosion.noisense.web.user.dto.UserDto;
import com.dosion.noisense.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "User-Controller", description = "회원 관리")
@RestController
@RequestMapping("/api/user") //변경
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(summary = "회원 추가", description = "새로운 회원을 추가합니다.")
  @PostMapping
  public ResponseEntity<UserDto> add(@RequestBody UserDto userDto) {
    log.info("[controller ] userNm : " + userDto.getUserNm());
    log.info("[controller ] nickname : " + userDto.getNickname());
    return ResponseEntity.ok(userService.add(userDto));
  }


  @Operation(summary = "회원 정보", description = "로그인한 회원의 정보를 조회합니다.")
  @GetMapping
  public ResponseEntity<UserDto> getUserInfo(
    @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long id = userDetails.getId();
    log.info("[getUserInfo] id : {}", id);
    log.info("[getUserInfo] username : {}", userDetails.getUsername());
    return ResponseEntity.ok(userService.getUserInfo(id));
  }


}

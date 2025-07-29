package com.dosion.noisense.web.user.controller;

import com.dosion.noisense.common.security.core.CustomUserDetails;
import com.dosion.noisense.web.user.dto.UserDto;
import com.dosion.noisense.web.user.dto.UserActivityStatsDto;
import com.dosion.noisense.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @Operation(summary = "회원 정보 수정", description = "로그인한 회원의 정보를 수정합니다.")
  @PutMapping
  public ResponseEntity<UserDto> updateUserInfo(
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @RequestBody UserDto userDto) {
    Long id = userDetails.getId();
    log.info("[updateUserInfo] id : {}", id);
    log.info("[updateUserInfo] nickname : {}", userDto.getNickname());
    return ResponseEntity.ok(userService.updateUser(id, userDto));
  }

  @Operation(summary = "회원 탈퇴", description = "로그인한 회원을 삭제합니다. 인증 데이터를 먼저 제거하고, 관련 테이블의 외래 키 참조를 제거한 후 회원 레코드를 삭제합니다.")
  @DeleteMapping
  public ResponseEntity<Void> deleteUser(
    @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long id = userDetails.getId();
    log.info("[deleteUser] id : {}", id);
    log.info("[deleteUser] username : {}", userDetails.getUsername());
    userService.deleteUser(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "사용자 활동 통계", description = "로그인한 사용자의 활동 통계를 조회합니다. 총 공감 수, 총 댓글 수, 지역 정보를 포함합니다.")
  @GetMapping("/activity-stats")
  public ResponseEntity<UserActivityStatsDto> getUserActivityStats(
    @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long id = userDetails.getId();
    log.info("[getUserActivityStats] id : {}", id);
    log.info("[getUserActivityStats] username : {}", userDetails.getUsername());
    return ResponseEntity.ok(userService.getUserActivityStats(id));
  }

}

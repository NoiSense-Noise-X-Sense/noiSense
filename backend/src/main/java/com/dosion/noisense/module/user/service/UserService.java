package com.dosion.noisense.module.user.service;

import com.dosion.noisense.module.district.entity.AdministrativeDistrict;
import com.dosion.noisense.web.user.dto.UserDto;
import com.dosion.noisense.web.user.dto.UserActivityStatsDto;
import com.dosion.noisense.module.user.entity.Users;
import com.dosion.noisense.module.user.repository.UserRepository;
import com.dosion.noisense.module.auth.repository.AuthRepository;
import com.dosion.noisense.module.board.repository.BoardRepository;
import com.dosion.noisense.module.board.entity.Board;
import com.dosion.noisense.module.comment.repository.CommentRepository;
import com.dosion.noisense.module.district.repository.DistrictRepository;
import com.dosion.noisense.module.district.entity.AutonomousDistrict;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AuthRepository authRepository;
  private final BoardRepository boardRepository;
  private final CommentRepository commentRepository;
  private final DistrictRepository districtRepository;

  @Transactional
  public UserDto add(UserDto userDto) {
    log.info("회원가입 UserNm : {}", userDto.getUserNm());
    log.info("회원가입 Nickname : {}", userDto.getNickname());

    if (userRepository.findByUserNm(userDto.getUserNm()).isPresent()){
      throw new RuntimeException("User already exists");
    }
    Users user = new Users();
    user.setUserNm(userDto.getUserNm());
    user.setNickname(userDto.getNickname());
    user.setEmail(userDto.getEmail());
    user.setAutonomousDistrict(userDto.getAutonomousDistrict());
    user.setAdministrativeDistrict(userDto.getAdministrativeDistrict());

    userRepository.save(user);
    userDto.setId(user.getId());
    return userDto;
  }



  @Transactional
  public UserDto getUserInfo(Long id){
    Users user = userRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. "));

    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUserNm(user.getUserNm());
    dto.setNickname(user.getNickname());
    dto.setEmail(user.getEmail());
    dto.setAutonomousDistrict(user.getAutonomousDistrict());
    dto.setAdministrativeDistrict(user.getAdministrativeDistrict());
    dto.setCreatedDate(user.getCreatedDate());
    dto.setModifiedDate(user.getModifiedDate());

    return dto;
  }

  @Transactional
  public UserDto updateUser(Long id, UserDto userDto) {
    log.info("회원정보 수정 ID : {}", id);
    log.info("회원정보 수정 Nickname : {}", userDto.getNickname());

    Users user = userRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // Update user fields if provided in the DTO
    if (userDto.getNickname() != null) {
      user.setNickname(userDto.getNickname());
    }
    if (userDto.getEmail() != null) {
      user.setEmail(userDto.getEmail());
    }
    if (userDto.getAutonomousDistrict() != null) {
      user.setAutonomousDistrict(userDto.getAutonomousDistrict());
    }
    if (userDto.getAdministrativeDistrict() != null) {
      user.setAdministrativeDistrict(userDto.getAdministrativeDistrict());
    }

    userRepository.save(user);

    // Return updated user info
    UserDto updatedDto = new UserDto();
    updatedDto.setId(user.getId());
    updatedDto.setUserNm(user.getUserNm());
    updatedDto.setNickname(user.getNickname());
    updatedDto.setEmail(user.getEmail());
    updatedDto.setAutonomousDistrict(user.getAutonomousDistrict());
    updatedDto.setAdministrativeDistrict(user.getAdministrativeDistrict());
    updatedDto.setCreatedDate(user.getCreatedDate());
    updatedDto.setModifiedDate(user.getModifiedDate());
    return updatedDto;
  }

  @Transactional
  public void deleteUser(Long id) {
    log.info("회원 삭제 ID : {}", id);

    // 1. 사용자 존재 확인
    Users user = userRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // 2. 먼저 사용자의 인증 데이터를 auth 테이블에서 제거
    if (authRepository.existsByUser(user)) {
      authRepository.findByUser(user).ifPresent(auth -> {
        log.info("사용자 인증 데이터 삭제: {}", user.getUserNm());
        authRepository.delete(auth);
      });
    }

    // 3. 관련 테이블에서 외래 키 참조만 제거 (관련 레코드 자체는 삭제하지 않음)
    // 3-1. Board 테이블에서 userId를 NULL로 설정
    log.info("Board 테이블에서 userId 참조 제거: {}", id);
    boardRepository.deleteByUserId(id);

    // 3-2. Comment 테이블에서 user 참조를 NULL로 설정
    log.info("Comment 테이블에서 user 참조 제거: {}", user.getUserNm());
    commentRepository.deleteByUserId(id);

    // 4. 마지막으로 사용자 레코드 삭제
    log.info("사용자 레코드 삭제: {}", user.getUserNm());
    userRepository.delete(user);

    log.info("회원 삭제 완료: {}", user.getUserNm());
  }

  @Transactional
  public UserActivityStatsDto getUserActivityStats(Long userId) {
    log.info("사용자 활동 통계 조회 ID : {}", userId);

    // 1. 사용자 정보 조회
    Users user = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // 2. 사용자의 모든 게시글 조회
    List<Board> userBoards = boardRepository.findByUserId(userId);

    // 3. 총 공감 수 계산 (모든 게시글의 empathyCount 합계)
    Long totalLikes = userBoards.stream()
      .mapToLong(board -> board.getEmpathyCount() != null ? board.getEmpathyCount() : 0L)
      .sum();

    // 4. 총 댓글 수 계산 (모든 게시글의 댓글 수 합계)
    Long totalComments = userBoards.stream()
      .mapToLong(board -> board.getComments() != null ? board.getComments().size() : 0L)
      .sum();

    // 5. 자치구 한글명 조회
    String autonomousDistrictKo = null;
    if (user.getAutonomousDistrict() != null) {
      autonomousDistrictKo = districtRepository.findAutonomousDistrictByCode(user.getAutonomousDistrict())
        .map(AutonomousDistrict::getNameKo)
        .orElse(user.getAutonomousDistrict()); // 찾지 못하면 코드 그대로 반환
    }

    // 6. 행정동 한글명 조회
    String administrativeDistrictKo = null;
    if (user.getAdministrativeDistrict() != null) {
      administrativeDistrictKo = districtRepository.findAdministrativeDistrictByCode(user.getAdministrativeDistrict())
        .map(AdministrativeDistrict::getNameKo)
        .orElse(user.getAdministrativeDistrict()); // 찾지 못하면 코드 그대로 반환
    }

    return UserActivityStatsDto.builder()
      .totalLikes(totalLikes)
      .totalComments(totalComments)
      .autonomousDistrictKo(autonomousDistrictKo)
      .administrativeDistrictKo(administrativeDistrictKo)
      .build();
  }

}

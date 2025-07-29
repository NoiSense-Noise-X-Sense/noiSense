package com.dosion.noisense.module.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.dosion.noisense.module.board.entity.Board;
import com.dosion.noisense.module.board.repository.BoardRepository;
import com.dosion.noisense.module.comment.entity.Comment;
import com.dosion.noisense.module.district.entity.AdministrativeDistrict;
import com.dosion.noisense.module.district.entity.AutonomousDistrict;
import com.dosion.noisense.module.district.repository.DistrictRepository;
import com.dosion.noisense.module.user.entity.Users;
import com.dosion.noisense.module.user.repository.UserRepository;
import com.dosion.noisense.web.user.dto.UserActivityStatsDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Disabled
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private BoardRepository boardRepository;
  @Mock
  private DistrictRepository districtRepository;

  @InjectMocks
  private UserService userService;

  private Users user;

  @BeforeEach
  void setup() {
    user = Users.builder()
      .id(1L)
      .autonomousDistrict("11090")
      .administrativeDistrict("11090700")
      .build();
  }
  @Test
  void 사용자_활동_통계_정상조회() {
    // given
    List<Board> mockBoards = Arrays.asList(
      Board.builder().empathyCount(10L).comments(Arrays.asList(new Comment(), new Comment())).build(),
      Board.builder().empathyCount(5L).comments(Collections.singletonList(new Comment())).build()
    );

    AutonomousDistrict gu = new AutonomousDistrict("11090", "강북구", "Gangbuk-gu");
    AdministrativeDistrict dong = new AdministrativeDistrict("11090700","미아동", "Mia-dong", gu);
    given(userRepository.findById(1L)).willReturn(Optional.of(user));
    given(boardRepository.findByUserId(1L)).willReturn(mockBoards);
    given(districtRepository.findAutonomousDistrictByCode("11090"))
      .willReturn(Optional.of(gu));
    given(districtRepository.findAdministrativeDistrictByCode("11090700"))
      .willReturn(Optional.of(dong));

    // when
    UserActivityStatsDto result = userService.getUserActivityStats(1L);

    // then
    assertThat(result.getTotalLikes()).isEqualTo(15L);
    assertThat(result.getTotalComments()).isEqualTo(3L);
    assertThat(result.getAutonomousDistrictKo()).isEqualTo("강북구");
    assertThat(result.getAdministrativeDistrictKo()).isEqualTo("미아동");
  }

  @Test
  void 사용자_조회_실패시_예외발생() {
    // given
    given(userRepository.findById(99L)).willReturn(Optional.empty());

    // when & then
    assertThrows(IllegalArgumentException.class, () -> {
      userService.getUserActivityStats(99L);
    });
  }


}

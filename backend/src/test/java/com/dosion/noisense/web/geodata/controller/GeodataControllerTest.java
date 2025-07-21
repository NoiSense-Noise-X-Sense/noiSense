package com.dosion.noisense.web.geodata.controller;

import com.dosion.noisense.BackendApplication;
import com.dosion.noisense.common.security.core.CustomUserDetailsService;
import com.dosion.noisense.module.geodata.fixture.GeodataFixture;
import com.dosion.noisense.module.geodata.service.GeodataService;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@Disabled // TODO: 왜 실패해
@SpringBootTest(classes = {BackendApplication.class/*, GeodataControllerTest.TestSecurityConfig.class*/})
@AutoConfigureMockMvc
@EnableJpaAuditing
@ComponentScan(
  basePackages = {"com.dosion.noisense.web.geodata"},
  excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.dosion\\.noisense\\.common\\.security\\..*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.dosion\\.noisense\\.common\\.oauth2\\..*")
  }
)
class GeodataControllerTest {

  @Autowired
  private MockMvc mockMvc;

//  private final Oauth2SecurityFilterChain
//  private final SecurityFilterChain filterChain = mock(SecurityFilterChain.class);
  private final CustomUserDetailsService customUserDetailsService= mock(CustomUserDetailsService.class);
  @Autowired
  private GeodataService geodataService;

  private final String token = "mockedToken";
  public final String PATH = "/api/v1/geodata";

  @BeforeEach
  void setup() {
  }

  @Test
  void getAutonomousPolygons_shouldReturnPolygonDtoList() throws Exception {
    // given
    List<BoundaryPolygonDto> mockData = GeodataFixture.getBoundaryPolygonDto();
    given(geodataService.getAutonomousPolygons()).willReturn(mockData);

    // when + then
    String uriTemplate = PATH + "/polygons/autonomous";

    mockMvc.perform(get(uriTemplate)
        .header("Authorization", "Bearer " + token) // 필수! 토큰 넣어야 필터 통과함
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].autonomousDistrict").value("11060"))
      .andExpect(jsonPath("$[1].autonomousDistrict").value("11070"));
  }


  @TestConfiguration
  public static class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
//        .csrf().disable()
        .authorizeHttpRequests(authz -> authz
          .anyRequest().permitAll()
        );
      return http.build();
    }
  }


  @TestConfiguration
  static class TestMockBeans {
    @Bean
    public GeodataService geodataService() {
      return mock(GeodataService.class);
    }
  }
}

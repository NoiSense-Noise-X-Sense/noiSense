package com.dosion.noisense.module.geodata.repository;

import com.dosion.noisense.module.district.entity.District;
import com.dosion.noisense.module.district.repository.DistrictRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.dosion.noisense.module.geodata.fixture.GeodataFixture.getAdministrativeDistricts;
import static com.dosion.noisense.module.geodata.fixture.GeodataFixture.getAutonomousDistricts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class DistrictRepositoryTest {

  @Mock
  private DistrictRepository districtRepository;
  List<District> autonomousDistricts;
  List<District> administrativeDistricts;

  @BeforeEach
  void setUp() {
    autonomousDistricts = getAutonomousDistricts();
    administrativeDistricts = getAdministrativeDistricts();
  }

  @Test
  void test_indAllAutonomousDistricts() {
    // given stub
    BDDMockito.given(districtRepository.findAllAutonomousDistricts()).willReturn(autonomousDistricts);

    List<District> result = districtRepository.findAllAutonomousDistricts();

    //. then
    assertThat(result).hasSize(3);
    assertAll(
            () -> assertThat(result.get(0)).extracting(District::getAutonomousDistrictCode, District::getAutonomousDistrictNameEn, District::getAutonomousDistrictNameKo).contains("11010", "Jongno-gu", "종로구"),
    () -> assertThat(result.get(1)).extracting(District::getAutonomousDistrictCode, District::getAutonomousDistrictNameEn, District::getAutonomousDistrictNameKo).contains("11020", "Jung-gu", "중구"),
    () -> assertThat(result.get(2)).extracting(District::getAutonomousDistrictCode, District::getAutonomousDistrictNameEn, District::getAutonomousDistrictNameKo).contains("11030", "Yongsan-gu", "용산구")
    );
  }

  @Test
  void test_indAllAdministrativeDistricts() {
    // given stub
    BDDMockito.given(districtRepository.findAllAdministrativeDistricts()).willReturn(administrativeDistricts);

    List<District> result = districtRepository.findAllAdministrativeDistricts();

    //. then
    assertThat(result).hasSize(3);
    assertAll(
            () -> assertThat(result.get(0)).extracting(District::getAdministrativeDistrictCode, District::getAdministrativeDistrictNameEn, District::getAdministrativeDistrictNameKo).contains("11010530", "Sajik-dong", "사직동"),
    () -> assertThat(result.get(1)).extracting(District::getAdministrativeDistrictCode, District::getAdministrativeDistrictNameEn, District::getAdministrativeDistrictNameKo).contains("11010540", "Samcheong-dong", "삼청동"),
    () -> assertThat(result.get(2)).extracting(District::getAdministrativeDistrictCode, District::getAdministrativeDistrictNameEn, District::getAdministrativeDistrictNameKo).contains("11010550", "Buam-dong", "부암동")
    );
  }
}

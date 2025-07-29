package com.dosion.noisense.module.district.repository;

import com.dosion.noisense.module.district.entity.AdministrativeDistrict;
import com.dosion.noisense.module.district.entity.AutonomousDistrict;
import com.dosion.noisense.module.district.entity.District;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;


@Repository
public interface DistrictRepository extends JpaRepository<AutonomousDistrict, String> {

  @Query(value = """
        SELECT
            a.code AS autonomousDistrictCode,
            a.name_en AS autonomousDistrictNameEn,
            a.name_ko AS autonomousDistrictNameKo,
            NULL AS administrativeDistrictCode,
            NULL AS administrativeDistrictNameEn,
            NULL AS administrativeDistrictNameKo,
            NULL AS parentDistrictCode
        FROM noisense.autonomous_district a
        """, nativeQuery = true)
  List<District> findAllAutonomousDistricts();

  @Query(value = """
        SELECT
            a.code AS autonomousDistrictCode,
            a.name_en AS autonomousDistrictNameEn,
            a.name_ko AS autonomousDistrictNameKo,
            d.code AS administrativeDistrictCode,
            d.name_en AS administrativeDistrictNameEn,
            d.name_ko AS administrativeDistrictNameKo,
            d.autonomous_district AS parentDistrictCode
        FROM noisense.administrative_district d
        JOIN noisense.autonomous_district a
          ON d.autonomous_district = a.code
        """, nativeQuery = true)
  List<District> findAllAdministrativeDistricts();

  Optional<AutonomousDistrict> findByNameKo(String nameKo);

  Optional<AutonomousDistrict> findByNameEn(String nameEn);

  // 영문 또는 한글명으로 찾기 (엔티티명에 맞게 JPQL로 수정)
  @Query("SELECT d FROM AutonomousDistrict d WHERE d.nameEn = :name OR d.nameKo = :name")
  Optional<AutonomousDistrict> findByNameEnOrNameKo(@Param("name") String name);

  // 자치구 코드로 찾기
  @Query("SELECT d FROM AutonomousDistrict d WHERE d.code = :code")
  Optional<AutonomousDistrict> findAutonomousDistrictByCode(@Param("code") String code);

  // 행정동 코드로 찾기
  @Query("SELECT d FROM AdministrativeDistrict d WHERE d.code = :code")
  Optional<AdministrativeDistrict> findAdministrativeDistrictByCode(@Param("code") String code);


  @Query(value = """
        SELECT
            a.code AS autonomousDistrictCode,
            a.name_en AS autonomousDistrictNameEn,
            a.name_ko AS autonomousDistrictNameKo,
            d.code AS administrativeDistrictCode,
            d.name_en AS administrativeDistrictNameEn,
            d.name_ko AS administrativeDistrictNameKo,
            d.autonomous_district AS parentDistrictCode
        FROM noisense.administrative_district d
        JOIN noisense.autonomous_district a
          ON d.autonomous_district = a.code
        WHERE d.autonomous_district = :autonomousDistrictCode
        """, nativeQuery = true)
  List<District> findAdministrativeDistrictsByAutonomousDistrictCode(@Param("autonomousDistrictCode") String autonomousDistrictCode);
}

package com.dosion.noisense.module.district.repository;

import com.dosion.noisense.module.district.entity.AutonomousDistrict;
import com.dosion.noisense.module.district.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


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

  List<District> findAllByOrderByNameKoAsc();

}

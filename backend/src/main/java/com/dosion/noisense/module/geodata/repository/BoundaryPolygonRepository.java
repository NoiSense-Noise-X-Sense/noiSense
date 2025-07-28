package com.dosion.noisense.module.geodata.repository;

import com.dosion.noisense.module.geodata.entity.BoundaryPolygon;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoundaryPolygonRepository extends JpaRepository<BoundaryPolygon, String> {

  //  // 검색 조건 별 데이터
  @Query("""
        SELECT bp
    FROM BoundaryPolygon bp
    """)
  List<BoundaryPolygon> findAllPolygons();

  @Query("""
    SELECT new com.dosion.noisense.web.geodata.dto.BoundaryPolygonProjection(
        bp.boundaryPolygonId,
        ad.nameEn,
        ad.nameKo,
        bp.administrativeDistrict,
        bp.autonomousDistrict,
        bp.boundaryType,
        bp.geometryFormat,
        bp.geometryType,
        bp.geometryCoordinate,
        bp.geometryCentroid
    )
    FROM BoundaryPolygon bp
    JOIN AutonomousDistrict ad ON bp.autonomousDistrict = ad.code
    WHERE bp.boundaryType = 'AUTONOMOUS_DISTRICT'
""")
  List<BoundaryPolygonProjection> findAutonomousPolygons();


  @Query("""
    SELECT new com.dosion.noisense.web.geodata.dto.BoundaryPolygonProjection(
        bp.boundaryPolygonId,
        ad.nameEn,
        ad.nameKo,
        bp.administrativeDistrict,
        bp.autonomousDistrict,
        bp.boundaryType,
        bp.geometryFormat,
        bp.geometryType,
        bp.geometryCoordinate,
        bp.geometryCentroid
    )
    FROM BoundaryPolygon bp
    JOIN AdministrativeDistrict ad ON bp.administrativeDistrict = ad.code
    WHERE bp.boundaryType = 'ADMINISTRATIVE_DISTRICT'
""")
  List<BoundaryPolygonProjection> findAdministrativePolygons();

}

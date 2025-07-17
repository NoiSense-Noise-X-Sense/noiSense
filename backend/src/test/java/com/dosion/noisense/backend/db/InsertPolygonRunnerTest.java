package com.dosion.noisense.backend.db;

import com.dosion.noisense.module.geodata.enums.GeometryFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Disabled /*개발 시에만 사용*/
@Description("자치구(autonomous_district), 행정동(administrative_district) 별 폴리곤을 테이블에 데이터 삽입하는 클래스")
public class InsertPolygonRunnerTest {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final String url = "jdbc:postgresql://localhost:5432/noisense";
  private static final String username = "root";
  private static final String password = "1234";

  @DisplayName("자치구 경계 좌표를 저장한다")
  @Test
  void insertGuTest() throws IOException {
    var inputStream = InsertPolygonRunnerTest.class.getClassLoader().getResourceAsStream("센서스경계_시군구_좌표.geojson");
    var root = objectMapper.readTree(inputStream);
    var features = root.get("features");

    try (var conn = getConnection()) {
      for (JsonNode feature : features) {
        var autonomousDistrictCode = feature.get("properties").get("SIGUNGU_CD").asText();
        var boundaryType = "autonomousDistrict";
        var geometryType = feature.get("geometry").get("type").asText();

        var coordinatesNode = feature.get("geometry").get("coordinates");
        var coordinatesString = objectMapper.writeValueAsString(coordinatesNode);

        String sql = """
          insert into noisense.boundary_polygon (
              boundary_polygon_id,
              autonomous_district,
              boundary_type,
              geometry_format,
              geometry_type,
              geometry_coordinate,
              created_date
          ) values (
                          nextval('noisense.boundary_polygon_boundary_polygon_id_seq'),
                          ?, cast(? as noisense.boundary_type), cast(? as noisense.geometry_format),
                          cast(? as noisense.geometry_type), ?, ?
          )
        """;

        conn.setAutoCommit(false);
        try (var pstmt = conn.prepareStatement(sql)) {
          if (!StringUtils.hasText(autonomousDistrictCode)) {
            throw new RuntimeException("rollback");
          }

          pstmt.setString(1, autonomousDistrictCode);
          pstmt.setString(2, boundaryType);
          pstmt.setString(3, GeometryFormat.GeoJSON.name());
          pstmt.setString(4, geometryType);
          pstmt.setString(5, coordinatesString);
          pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
          pstmt.executeUpdate();
          conn.commit();
        } catch (Exception e) {
          conn.rollback();
          throw e;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @DisplayName("행정동 경계 좌표를 저장한다")
  @Test
  void insertDongTest() throws IOException {
    var inputStream = InsertPolygonRunnerTest.class.getClassLoader().getResourceAsStream("센서스경계_읍면동_좌표.geojson");
    var root = objectMapper.readTree(inputStream);
    var features = root.get("features");

    System.out.println(String.format("features size: %d", features.size()) );


    try (var conn = getConnection()) {
      for (JsonNode feature : features) {
        var administrativeDistrictCode = feature.get("properties").get("ADM_CD").asText(); // administrative_district.autonomous_district 참조

        var autonomousDistrictCode = administrativeDistrictCode.substring(0, 5);

        var boundaryType = "administrativeDistrict";
        var geometryType = feature.get("geometry").get("type").asText();

        var coordinatesNode = feature.get("geometry").get("coordinates");
        var coordinatesString = objectMapper.writeValueAsString(coordinatesNode);

        String sql = """
          insert into noisense.boundary_polygon (
              boundary_polygon_id,
              autonomous_district,
              administrative_district,
              boundary_type,
              geometry_format,
              geometry_type,
              geometry_coordinate,
              created_date
          ) values (
                          nextval('noisense.boundary_polygon_boundary_polygon_id_seq'),
                          ?, ?, cast(? as noisense.boundary_type), cast(? as noisense.geometry_format),
                          cast(? as noisense.geometry_type), ?, ?
          )
        """;

        conn.setAutoCommit(false);
        try (var pstmt = conn.prepareStatement(sql)) {
          if (!StringUtils.hasText(autonomousDistrictCode) || !StringUtils.hasText(administrativeDistrictCode)) {
            throw new RuntimeException("rollback");
          }

          pstmt.setString(1, autonomousDistrictCode);
          pstmt.setString(2, administrativeDistrictCode);
          pstmt.setString(3, boundaryType);
          pstmt.setString(4, GeometryFormat.GeoJSON.name());
          pstmt.setString(5, geometryType);
          pstmt.setString(6, coordinatesString);
          pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
          pstmt.executeUpdate();
          conn.commit();
        } catch (Exception e) {
          conn.rollback();
          throw e;
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, username, password);
  }

}

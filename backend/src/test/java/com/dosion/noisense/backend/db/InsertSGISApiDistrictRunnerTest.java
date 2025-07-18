package com.dosion.noisense.backend.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Disabled /*개발 시에만 사용*/
@Description("자치구, 행정동 별 경계 좌표 정보를 boundary_polygon 테이블에 데이터 삽입하는 클래스")
public class InsertSGISApiDistrictRunnerTest {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final String url = "jdbc:postgresql://localhost:5432/noisense";
  private static final  String username = "root";
  private static final String password = "1234";

  @DisplayName("자치구 경계 좌표를 저장한다")
  @Test
  void insertGuTest() throws IOException {
    var inputStream = InsertSGISApiDistrictRunnerTest.class.getClassLoader().getResourceAsStream("센서스경계_시군구_좌표.geojson");
    var root = objectMapper.readTree(inputStream);
    var features = root.get("features");

    try (var conn = getConnection()) {
      for (JsonNode feature : features) {
        var name = feature.get("properties").get("SIGUNGU_CD").asText();
        var coords = objectMapper.writeValueAsString(feature.get("geometry").get("coordinates"));

        var sql = """
                    insert into noisense.boundary_polygon (
                        autonomous_district,
                        administrative_district,
                        boundary_type,
                        geometry_format,
                        geometry_type,
                        geometry_coordinate,
                        created_date
                    ) values (?, ?, '자치구', 'GeoJSON', 'MultiPolygon', ?, now())
                """;

        conn.setAutoCommit(false);
        try (var pstmt = conn.prepareStatement(sql)) {
          pstmt.setString(1, name);
          pstmt.setNull(2, java.sql.Types.VARCHAR);
          pstmt.setString(3, coords);
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


  @DisplayName("행정동 경계 좌표를 저장한다")
  @Test
  void insertDongTest() throws IOException {
    var inputStream = InsertSGISApiDistrictRunnerTest.class.getClassLoader().getResourceAsStream("센서스경계_읍면동_좌표.geojson");
    var root = objectMapper.readTree(inputStream);
    var features = root.get("features");

    try (var conn = getConnection()) {
      for (JsonNode feature : features) {

        var admCd = feature.get("properties").get("ADM_CD").asText();
        String autonomousDistrict = admCd.substring(0, 5);           // "11240"
        String administrativeDistrict = admCd;                        // "11240530"

        var name = feature.get("properties").get("ADM_NM").asText();
        var coords = objectMapper.writeValueAsString(feature.get("geometry").get("coordinates"));

        var sql = """
                    insert into noisense.boundary_polygon (
                        autonomous_district,
                        administrative_district,
                        boundary_type,
                        geometry_format,
                        geometry_type,
                        geometry_coordinate,
                        created_date
                    ) values (?, ?, '행정동', 'GeoJSON', 'MultiPolygon', ?, now())
                """;

        conn.setAutoCommit(false);
        try (var pstmt = conn.prepareStatement(sql)) {
          pstmt.setString(1, autonomousDistrict);
          pstmt.setString(2, administrativeDistrict);
          pstmt.setString(3, coords);
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

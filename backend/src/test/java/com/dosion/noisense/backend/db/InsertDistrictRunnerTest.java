package com.dosion.noisense.backend.db;

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

import com.dosion.noisense.web.common.parser.AddressParser;


@Disabled /*개발 시에만 사용*/
@Description("자치구(autonomous_district), 행정동(administrative_district) 별 코드와 국영문명을 각 테이블에 데이터 삽입하는 클래스")
public class InsertDistrictRunnerTest {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final String url = "jdbc:postgresql://localhost:5432/noisense";
  private static final String username = "root";
  private static final String password = "1234";

  @DisplayName("자치구만 분리하여 등록한다")
  @Test
  void insertGuTest() throws IOException {
    var inputStream = InsertDistrictRunnerTest.class.getClassLoader().getResourceAsStream("서울시_행정구역만.geojson");
    var root = objectMapper.readTree(inputStream);
    var features = root.get("features");

    try (var conn = getConnection()) {
      for (JsonNode feature : features) {
        var code = feature.get("properties").get("adm_cd").asText();
        var nameEn = feature.get("properties").get("addr_en").asText();
        var nameKo = feature.get("properties").get("adm_nm").asText();

        String newEn = AddressParser.joinExcludingLastOne(nameEn.split(" "));
        String newKo = AddressParser.joinExcludingFirstOne(nameKo.split(" "));

        var sql = """
              insert into noisense.autonomous_district (
                  code,
                  name_en,
                  name_ko
              ) values (?, ?, ?)
          """;

        conn.setAutoCommit(false);
        try (var pstmt = conn.prepareStatement(sql)) {
          if (!StringUtils.hasText(newKo) || !StringUtils.hasText(newEn)) {
            throw new RuntimeException("rollback");
          }

          pstmt.setString(1, code);
          pstmt.setString(2, newKo);
          pstmt.setString(3, newEn);
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


  @DisplayName("행정동만 분리하여 저장한다.")
  @Test
  void insertDongTest() throws IOException {
    var inputStream = InsertDistrictRunnerTest.class.getClassLoader().getResourceAsStream("서울시_행정구역_행정동까지.geojson");
    var root = objectMapper.readTree(inputStream);
    var features = root.get("features");

    try (var conn = getConnection()) {
      for (JsonNode feature : features) {
        var code = feature.get("properties").get("adm_cd").asText();
        var nameEn = feature.get("properties").get("addr_en").asText();
        var nameKo = feature.get("properties").get("adm_nm").asText();

        String newEn = AddressParser.joinExcludingLastTwo(nameEn.split(" "));
        String newKo = AddressParser.joinExcludingFirstTwo(nameKo.split(" "));

        var autonomousDistrictCode = code.substring(0, 5);

        var sql = """
              insert into "noisense"."administrative_district" (
                  code,
                  name_en,
                  name_ko,
                  autonomous_district
              ) values (?, ?, ?, ?)
          """;

        conn.setAutoCommit(false);
        try (var pstmt = conn.prepareStatement(sql)) {
          if (!StringUtils.hasText(newKo) || !StringUtils.hasText(newEn)) {
            throw new RuntimeException("rollback");
          }

          pstmt.setString(1, code);
          pstmt.setString(2, newEn);
          pstmt.setString(3, newKo);
          pstmt.setString(4, autonomousDistrictCode);
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

//package com.dosion.noisense.common.util;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import lombok.extern.slf4j.Slf4j;
//import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
//import org.geotools.api.referencing.operation.MathTransform;
//import org.geotools.referencing.CRS;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//@Slf4j
//public class CoordinateConverterTest {
//
//  private static final CoordinateReferenceSystem CRS_5186;
//  private static final CoordinateReferenceSystem CRS_WGS84;
//  private static final MathTransform TRANSFORM;
//
//  @Disabled
//  @DisplayName("전체 자치구 좌표 변환하여 파일로 저장")
//  @Test
//  void read_5181_gu_geojson_convert_to_wgs84_test() throws IOException {
//    ObjectMapper mapper = new ObjectMapper();
//    String input = Files.readString(Path.of("src/test/resources/센서스_시군구_5181.geojson"));
////    String input = Files.readString(Path.of("src/test/resources/센서스_시군구_5181.geojson"));
////    String input = "[[[[206125.533683299290715,556523.929393474012613],[206126.011990877217613,556434.116097901947796]]]]";
//    JsonNode raw = JsonUtilTest.toJsonNode(input);
//    ArrayNode features = (ArrayNode) raw.get("features");
//
//    // 2. features 순회하면서 좌표 변환
//    for (JsonNode feature : features) {
//      JsonNode geom = feature.get("geometry");
//      JsonNode coords = geom.get("coordinates");
//
//      // 좌표 추출 및 변환
//      List<double[]> tmCoords = extractAllCoordinates(coords);
//      List<double[]> wgsCoords = convert5186ListToWGS84(tmCoords);
//
//      // 변환된 좌표를 GeoJSON 구조로 재구성
//      JsonNode converted = buildNestedJsonFromCoordinates(mapper, coords, wgsCoords.iterator());
//
//      // geometry 안의 coordinates 필드 교체
//      ((ObjectNode) geom).set("coordinates", converted);
//    }
//
//    // 3. 결과를 파일로 저장
//    Path outputPath = Path.of("src/test/resources/센서스_시군구_wgs84.geojson");
////    Path outputPath = Path.of("src/test/resources/센서스_시군구_wgs84.geojson");
//    Files.writeString(outputPath, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(raw));
//  }
//
//  @Disabled
//  @DisplayName("전체  행정동 경계 좌표 변환하여 파일로 저장")
//  @Test
//  void read_5181_dong_geojson_convert_to_wgs84_test() throws IOException {
//    ObjectMapper mapper = new ObjectMapper();
//    String input = Files.readString(Path.of("src/test/resources/센서스_읍면동_5181.geojson"));
////    String input = Files.readString(Path.of("src/test/resources/센서스_시군구_5181.geojson"));
////    String input = "[[[[206125.533683299290715,556523.929393474012613],[206126.011990877217613,556434.116097901947796]]]]";
//    JsonNode raw = JsonUtilTest.toJsonNode(input);
//    ArrayNode features = (ArrayNode) raw.get("features");
//
//    // 2. features 순회하면서 좌표 변환
//    for (JsonNode feature : features) {
//      JsonNode geom = feature.get("geometry");
//      JsonNode coords = geom.get("coordinates");
//
//      // 좌표 추출 및 변환
//      List<double[]> tmCoords = extractAllCoordinates(coords);
//      List<double[]> wgsCoords = convert5186ListToWGS84(tmCoords);
//
//      // 변환된 좌표를 GeoJSON 구조로 재구성
//      JsonNode converted = buildNestedJsonFromCoordinates(mapper, coords, wgsCoords.iterator());
//
//      // geometry 안의 coordinates 필드 교체
//      ((ObjectNode) geom).set("coordinates", converted);
//    }
//
//    // 3. 결과를 파일로 저장
//    Path outputPath = Path.of("src/test/resources/센서스_읍면동_wgs84.geojson");
////    Path outputPath = Path.of("src/test/resources/센서스_시군구_wgs84.geojson");
//    Files.writeString(outputPath, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(raw));
//  }
//
//  @DisplayName("단일 구역 경계 좌표 변환")
//  @Test
//  void read_5181_coordinates_convert_to_wgs84_test() throws IOException {
//    String input = Files.readString(Path.of("src/test/resources/long_input.json"));
////    String input = "[[[[206125.533683299290715,556523.929393474012613],[206126.011990877217613,556434.116097901947796]]]]";
//    JsonNode raw = JsonUtilTest.toJsonNode(input);
//
//    List<double[]> tmCoords = extractAllCoordinates(raw);
//    List<double[]> wgsCoords = convert5186ListToWGS84(tmCoords);
//
//    wgsCoords.forEach(coord -> System.out.printf("%.8f, %.8f\n", coord[1], coord[0]));
//  }
//
//  @DisplayName("변환 함수 테스트")
//  @Test
//  void converttest() {
//    double[] wgs = convert5186ToWGS84(193625.29630944124, 543960.2967875343);
//    double[] wgs2 = convert5186ToWGS84(206125.533683299290715, 556523.929393474012613);
//    System.out.printf("WGS84 1: 경도 %.8f, 위도 %.8f\n", wgs[0], wgs[1]);
//    System.out.printf("WGS84 2: 경도 %.8f, 위도 %.8f\n", wgs2[0], wgs2[1]);
//  }
//
//  public static JsonNode unwrapToArrayOfPoints(JsonNode node) {
//    while (node.isArray() && node.size() > 0 && node.get(0).isArray()) {
//      // 아직 숫자 좌표까지 안 내려왔으면 더 들어감
//      node = node.get(0);
//    }
//    return node;
//  }
//
//  public static List<double[]> extractAllCoordinates(JsonNode node) {
//    List<double[]> result = new ArrayList<>();
//
//    if (node.isArray()) {
//      if (node.size() == 2 && node.get(0).isNumber() && node.get(1).isNumber()) {
//        // [x, y] 형태일 때만 추가
//        result.add(new double[]{
//          node.get(0).asDouble(),
//          node.get(1).asDouble()
//        });
//      } else {
//        // 더 중첩된 배열이면 재귀 순회
//        for (JsonNode child : node) {
//          result.addAll(extractAllCoordinates(child));
//        }
//      }
//    }
//
//    return result;
//  }
//
//
//  /* 좌표계 정의 */
//  static {
//    try {
//      String wkt5186 = """
//            PROJCS["Korea 2000 / Central Belt",
//              GEOGCS["Korea 2000",
//                DATUM["Geocentric Datum of Korea",
//                  SPHEROID["GRS 1980",6378137,298.257222101,
//                    AUTHORITY["EPSG","7019"]],
//                  AUTHORITY["EPSG","6737"]],
//                PRIMEM["Greenwich",0,
//                  AUTHORITY["EPSG","8901"]],
//                UNIT["degree",0.0174532925199433,
//                  AUTHORITY["EPSG","9122"]],
//                AUTHORITY["EPSG","4737"]],
//              PROJECTION["Transverse_Mercator"],
//              PARAMETER["latitude_of_origin",38],
//              PARAMETER["central_meridian",127],
//              PARAMETER["scale_factor",1],
//              PARAMETER["false_easting",200000],
//              PARAMETER["false_northing",500000],
//              UNIT["metre",1,
//                AUTHORITY["EPSG","9001"]],
//              AXIS["Easting",EAST],
//              AXIS["Northing",NORTH],
//              AUTHORITY["EPSG","5186"]]
//        """;
//      String wkt4326 = """
//            GEOGCS["WGS 84",
//              DATUM["World Geodetic System 1984",
//                SPHEROID["WGS 84",6378137,298.257223563,
//                  AUTHORITY["EPSG","7030"]],
//                AUTHORITY["EPSG","6326"]],
//              PRIMEM["Greenwich",0,
//                AUTHORITY["EPSG","8901"]],
//              UNIT["degree",0.0174532925199433,
//                AUTHORITY["EPSG","9122"]],
//              AXIS["Latitude",NORTH],
//              AXIS["Longitude",EAST],
//              AUTHORITY["EPSG","4326"]]
//        """;
//
//      CRS_5186 = CRS.parseWKT(wkt5186);
//      CRS_WGS84 = CRS.parseWKT(wkt4326);
//      TRANSFORM = CRS.findMathTransform(CRS_5186, CRS_WGS84, true);
//    } catch (Exception e) {
//      throw new RuntimeException("좌표계 초기화 실패", e);
//    }
//  }
//
//  /**
//   * 단일 TM 좌표 → WGS84 위경도 (경도, 위도 순서)
//   */
//  public static double[] convert5186ToWGS84(double x, double y) {
//    try {
//      double[] src = new double[]{x, y};
//      double[] dst = new double[2];
//      TRANSFORM.transform(src, 0, dst, 0, 1);
//      return dst;
//    } catch (Exception e) {
//      throw new RuntimeException("좌표 변환 실패: " + x + ", " + y, e);
//    }
//  }
//
//  /**
//   * 여러 TM 좌표 일괄 변환
//   */
//  public static List<double[]> convert5186ListToWGS84(List<double[]> tmCoords) {
//    List<double[]> result = new ArrayList<>();
//    for (double[] coord : tmCoords) {
//      result.add(convert5186ToWGS84(coord[0], coord[1]));
//    }
//    return result;
//  }
//
//  // 변환된 좌표 리스트를 원래 구조에 맞게 다시 중첩 배열로 쌓음
//  public static JsonNode buildNestedJsonFromCoordinates(ObjectMapper mapper, JsonNode original, Iterator<double[]> coordIterator) {
//    if (original.isArray()) {
//      if (original.size() == 2 && original.get(0).isNumber()) {
//        double[] coord = coordIterator.next();
//        ArrayNode node = mapper.createArrayNode();
//        node.add(coord[0]); // lon
//        node.add(coord[1]); // lat
//        return node;
//      } else {
//        ArrayNode node = mapper.createArrayNode();
//        for (JsonNode child : original) {
//          node.add(buildNestedJsonFromCoordinates(mapper, child, coordIterator));
//        }
//        return node;
//      }
//    }
//    return original; // fallback
//  }
//
//}

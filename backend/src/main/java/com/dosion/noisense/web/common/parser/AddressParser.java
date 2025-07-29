package com.dosion.noisense.web.common.parser;

import java.util.Arrays;

/*
* 데이터 샘플
* 서울특별시: 11
*  자치구:
*  서울특별시 종로구: 11010
 * Sajik-dong Jongno-gu Seoul
 * 행정동:
* 서울특별시 종로구 사직동: 11010530
* Sajik-dong Jongno-gu Seoul
* */
public class AddressParser {
  public static String joinExcludingLastTwo(String[] strings) {
    if (strings.length <= 2) {
      return "";
    }
    return String.join(" ", Arrays.copyOfRange(strings, 0, strings.length - 2));
  }

  public static String joinExcludingFirstTwo(String[] strings) {
    if (strings.length <= 2) {
      return "";
    }
    return String.join(" ", Arrays.copyOfRange(strings, 2, strings.length));
  }
  public static String joinExcludingLastOne(String[] strings) {
    if (strings.length <= 1) {
      return "";
    }
    return String.join(" ", Arrays.copyOfRange(strings, 0, strings.length - 1));
  }

  public static String joinExcludingFirstOne(String[] strings) {
    if (strings.length <= 1) {
      return "";
    }
    return String.join(" ", Arrays.copyOfRange(strings, 1, strings.length));
  }
}

package com.dosion.noisense.web.common.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.dosion.noisense.web.common.parser.AddressParser.joinExcludingFirstTwo;
import static com.dosion.noisense.web.common.parser.AddressParser.joinExcludingLastTwo;

class AddressParserTest {

  @Test
  void substr_en() {
    String[] nameEn1 = "Cheongunhyoja-dong Jongno-gu Seoul".split(" ");
    String[] nameEn2 = "Imun 1(il)-dong Dongdaemun-gu Seoul".split(" ");

    Assertions.assertAll(
      () -> org.assertj.core.api.Assertions.assertThat(joinExcludingLastTwo(nameEn1)).isEqualTo("Cheongunhyoja-dong"),
      () -> org.assertj.core.api.Assertions.assertThat(joinExcludingLastTwo(nameEn2)).isEqualTo("Imun 1(il)-dong")
    );
  }

  @Test
  void substr_ko() {
    String[] nameKo1 = "서울특별시 종로구 청운효자동".split(" ");
    String[] nameKo2 = "서울특별시 동대문구 이문1동".split(" ");

    Assertions.assertAll(
      () -> org.assertj.core.api.Assertions.assertThat(joinExcludingFirstTwo(nameKo1)).isEqualTo("청운효자동"),
      () -> org.assertj.core.api.Assertions.assertThat(joinExcludingFirstTwo(nameKo2)).isEqualTo("이문1동")
    );
  }
}

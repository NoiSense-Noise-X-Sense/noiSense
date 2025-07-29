package com.dosion.noisense.web.board.elasticsearch.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 강사님 코드는 검색 조건(예: 키워드, 페이지, 사이즈 등) 을 Controller의 @RequestParam으로 직접 하나하나 받는 방식
 * DTO로 한 번에 묶어서 받는 방식으로 구현하기 위해 다음 클래스 추가
 */

@Getter
@Setter
public class BoardEsSearchRequestDto {
  private String keyword;
  private int page;
  private int size;
}

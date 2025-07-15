package com.dosion.noisense.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private int code; // 상태코드
  private String message; // 커스텀 에러 메시지
  private String detail; // 실제 에러 메시지

}

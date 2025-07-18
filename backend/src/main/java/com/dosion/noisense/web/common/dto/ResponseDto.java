package com.dosion.noisense.web.common.dto;

import com.dosion.noisense.web.common.enums.ResponseCode;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseDto<T> {

  private String code;       // 예: "Success" / "Fail"
  private String message;    // 예: 성공/실패 메시지
  private T data;            // 실제 응답 데이터
  private String error;      // 에러 상세 정보 (필요 없으면 빈 문자열)

  @Builder
  public ResponseDto(String code, String message, T data, String error) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.error = error;
  }

  // 성공 응답
  public static <T> ResponseDto<T> success(T data) {
    return ResponseDto.<T>builder()
      .code(ResponseCode.Success.name())
      .message("")
      .data(data)
      .error("")
      .build();
  }

  // 성공 응답 (메시지 포함)
  public static <T> ResponseDto<T> success(T data, String message) {
    return ResponseDto.<T>builder()
      .code(ResponseCode.Success.name())
      .message(message)
      .data(data)
      .error("")
      .build();
  }

  // 실패 응답
  public static <T> ResponseDto<T> fail(String message, String error) {
    return ResponseDto.<T>builder()
      .code(ResponseCode.Fail.name())
      .message(message)
      .data(null)
      .error(error)
      .build();
  }
}

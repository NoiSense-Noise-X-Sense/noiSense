package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "편차 소음 DTO")
public class MaxNoiseDto {

    @Schema(description = "행정구 or 행정동")
    String region;

    @Schema(description = "최대 소음 시간")
    LocalDateTime maxNoiseTime;

    @Schema(description = "최대 소음")
    Double maxNoise;
}

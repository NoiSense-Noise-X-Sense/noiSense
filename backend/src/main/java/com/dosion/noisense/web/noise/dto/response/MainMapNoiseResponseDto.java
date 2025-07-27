package com.dosion.noisense.web.noise.dto.response;

import com.dosion.noisense.web.noise.dto.AvgNoiseWithPerceivedDto;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainMapNoiseResponseDto {

  private List<AvgNoiseWithPerceivedDto> autonomousDistricts;
}

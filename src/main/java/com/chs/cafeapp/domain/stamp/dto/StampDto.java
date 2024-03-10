package com.chs.cafeapp.domain.stamp.dto;

import com.chs.cafeapp.domain.stamp.entity.Stamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StampDto {
  private long id;
  private String userLoginId;
  private long stampNumbers;

  public static StampDto of(Stamp stamp) {
    return StampDto.builder()
                  .id(stamp.getId())
                  .userLoginId(stamp.getMember().getLoginId())
                  .stampNumbers(stamp.getStampNumbers())
                  .build();
  }
}

package com.chs.cafeapp.stamp.dto;

import com.chs.cafeapp.stamp.entity.Stamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StampDto {
  private long id;
  private String userLoginId;
  private long stampNumbers;

  public static StampDto of(Stamp stamp) {
    return StampDto.builder()
                  .id(stamp.getId())
                  .userLoginId(stamp.getUser().getLoginId())
                  .stampNumbers(stamp.getStampNumbers())
                  .build();
  }
}

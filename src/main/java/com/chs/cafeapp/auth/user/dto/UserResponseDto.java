package com.chs.cafeapp.auth.user.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {
  private String loginId;
  private String nickName;
  private LocalDateTime createDateTime;
  private String message;
  public void setMessage(String message) {
    this.message = message;
  }
}

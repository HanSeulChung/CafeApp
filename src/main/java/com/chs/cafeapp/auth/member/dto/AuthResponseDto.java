package com.chs.cafeapp.auth.member.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponseDto {
  private String loginId;
  private LocalDateTime createDateTime;
  private String message;
  public void setMessage(String message) {
    this.message = message;
  }
}

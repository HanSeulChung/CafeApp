package com.chs.cafeapp.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
  private String loginId;
  private LocalDateTime createDateTime;
  private String message;
  public void setMessage(String message) {
    this.message = message;
  }
}

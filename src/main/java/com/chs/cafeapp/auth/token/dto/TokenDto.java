package com.chs.cafeapp.auth.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenDto {
  private String grantType;
  private String accessToken;
  private Long accessTokenExpiresIn;
  private String refreshToken;
}

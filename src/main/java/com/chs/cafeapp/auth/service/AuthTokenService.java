package com.chs.cafeapp.auth.service;

import com.chs.cafeapp.auth.dto.LogOutResponse;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;

public interface AuthTokenService {

  /**
   * access 토큰 재발급
   */
  TokenResponseDto reIssue(TokenRequestDto tokenRequestDto);

  /**
   * 로그아웃
   */
  LogOutResponse logOut(String accessToken);
}

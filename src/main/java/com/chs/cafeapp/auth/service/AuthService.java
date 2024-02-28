package com.chs.cafeapp.auth.service;

import com.chs.cafeapp.auth.dto.AuthResponseDto;
import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.dto.SignInRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;

public interface AuthService {

  /**
   * 로그인
   */
  TokenResponseDto signIn(SignInRequestDto signInRequestDto);

  /**
   * 메일 인증
   */
  AuthResponseDto emailAuth(String email, String certifiedNumber);

  /**
   * 비밀번호 변경
   */
  PasswordEditResponse changePassword(PasswordEditInput passwordEditInput);
}

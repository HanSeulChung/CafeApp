package com.chs.cafeapp.auth.service;

import com.chs.cafeapp.auth.dto.LogOutResponse;
import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.member.dto.SignInRequestDto;
import com.chs.cafeapp.auth.member.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.member.dto.AuthResponseDto;

public interface AuthService {

  /**
   * 일반 회원가입
   */
  AuthResponseDto signUp(SignUpRequestDto signUpRequestDto);

  /**
   * 로그인
   */
  TokenResponseDto signIn(SignInRequestDto signInRequestDto);

  /**
   * 소셜 로그인 및 회원가입
   */

  /**
   * 메일 인증
   */
  AuthResponseDto emailAuth(String uuid);

  /**
   * 비밀번호 변경
   */
  PasswordEditResponse changePassword(PasswordEditInput passwordEditInput);
}

package com.chs.cafeapp.auth.service;

import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.user.dto.SignInRequestDto;
import com.chs.cafeapp.auth.user.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.user.dto.UserResponseDto;

public interface AuthService {

  /**
   * 일반 회원가입
   */
  UserResponseDto signUp(SignUpRequestDto signUpRequestDto);

  /**
   * 메일 인증
   */
  UserResponseDto emailAuth(String uuid);

  /**
   * 사용자 일반 로그인
   */
  TokenResponseDto signIn(SignInRequestDto signInRequestDto);


  /**
   * 소셜 로그인 및 회원가입
   */

  /**
   * access 토큰 재발급
   */
  TokenResponseDto reIssue(TokenRequestDto tokenRequestDto);
}

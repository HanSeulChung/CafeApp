package com.chs.cafeapp.auth.service;

import com.chs.cafeapp.auth.dto.LogOutResponse;
import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.member.dto.SignInRequestDto;
import com.chs.cafeapp.auth.member.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.member.dto.MemberResponseDto;

public interface AuthService {

  /**
   * 일반 회원가입
   */
  MemberResponseDto signUp(SignUpRequestDto signUpRequestDto);

  /**
   * 메일 인증
   */
  MemberResponseDto emailAuth(String uuid);

  /**
   * 사용자 일반 로그인
   */
  TokenResponseDto signIn(SignInRequestDto signInRequestDto);

  /**
   * 카페 관계자 로그인
   */
  TokenResponseDto adminSignIn(SignInRequestDto signInRequestDto);

  /**
   * 소셜 로그인 및 회원가입
   */

  /**
   * access 토큰 재발급
   */
  TokenResponseDto reIssue(TokenRequestDto tokenRequestDto);

  /**
   * 로그아웃
   */
  LogOutResponse logOut(String accessToken);

  /**
   * 비밀번호 변경
   */
  PasswordEditResponse changePassword(PasswordEditInput passwordEditInput);
}

package com.chs.cafeapp.auth.service;

import com.chs.cafeapp.auth.user.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.user.dto.UserResponseDto;
import javax.servlet.http.HttpServletRequest;

public interface AuthService {

  /**
   * 일반 회원가입
   */
  UserResponseDto signup(SignUpRequestDto signUpRequestDto);

  /**
   * 메일 인증
   */
  UserResponseDto emailAuth(String uuid);

  /**
   * 일반 로그인
   */

  /**
   * 소셜 로그인 및 회원가입
   */
}

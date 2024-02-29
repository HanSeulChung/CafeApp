package com.chs.cafeapp.auth.service;

import com.chs.cafeapp.auth.dto.AuthResponseDto;
import com.chs.cafeapp.auth.dto.LogOutResponse;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.type.UserType;
import com.chs.cafeapp.global.mail.dto.EmailAuthRequest;
import com.chs.cafeapp.global.mail.dto.EmailRequest;
import java.security.NoSuchAlgorithmException;
import javax.mail.MessagingException;

public interface AuthCommonService {

  /**
   * 메일 인증
   */
  Boolean emailAuth(String email, String certifiedNumber);

  /**
   * 메일 중복 체크
   */
  Boolean checkEmail(String email, UserType userType);

  /**
   * 인증 메일 재전송
   */
  String reEmail(String email, UserType userType) throws NoSuchAlgorithmException, MessagingException;

  /**
   * access 토큰 재발급
   */
  TokenResponseDto reIssue(TokenRequestDto tokenRequestDto);

  /**
   * 로그아웃
   */
  LogOutResponse logOut(String accessToken);
}

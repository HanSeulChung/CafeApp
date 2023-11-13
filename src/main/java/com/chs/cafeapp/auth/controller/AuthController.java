package com.chs.cafeapp.auth.controller;

import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.user.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  /**
   * 일반 회원가입 Controller
   * @param signUpRequestDto: 일반 회원가입 입력 값
   * @return UserResponseDto: 아이디, 생성 날짜, 가입 축하 멘트
   */
  @PostMapping("sing-up")
  public ResponseEntity<UserResponseDto> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
    return ResponseEntity.ok(authService.signup(signUpRequestDto));
  }
}

package com.chs.cafeapp.auth.controller;

import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.token.dto.TokenDto;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.user.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.user.dto.SignInRequestDto;
import com.chs.cafeapp.auth.user.dto.UserResponseDto;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  @PostMapping("/sing-up")
  public ResponseEntity<UserResponseDto> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
    return ResponseEntity.ok(authService.signUp(signUpRequestDto));
  }

  @GetMapping("/email-auth")
  public ResponseEntity<UserResponseDto> emailAuth(HttpServletRequest request) {
    return ResponseEntity.ok(authService.emailAuth(request.getParameter("id")));
  }

  @PostMapping("/sign-in")
  public ResponseEntity<TokenResponseDto> signin(@RequestBody SignInRequestDto signInRequestDto) {
    return ResponseEntity.ok(authService.signIn(signInRequestDto));
  }
  @PostMapping("/reissue")
  public ResponseEntity<TokenResponseDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
    return ResponseEntity.ok(authService.reIssue(tokenRequestDto));
  }
}

package com.chs.cafeapp.auth.controller;

import com.chs.cafeapp.auth.dto.LogOutResponse;
import com.chs.cafeapp.auth.service.impl.AuthServiceImpl;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
  private final AuthServiceImpl authService;

  /**
   * 로그아웃 Controller
   * @exception CustomException: 유효하지 않은 token값일 경우, 이미 로그아웃된 사용자일 경우 CustomException 발생
   * @return LogOutResponse: logout한 loginId, "로그아웃 되었습니다." String 값
   */
  @PostMapping("/logout")
  public ResponseEntity<LogOutResponse> logOut(@RequestHeader("Authorization") String accessToken) {
    return ResponseEntity.ok(authService.logOut(accessToken));
  }
  /**
   * access Token 재발급 Controller
   * @param tokenRequestDto: 기존에 로그인때 받았던 tokenResponseDto(accessToken, refreshToken)값
   * @exception CustomException: refreshToken이 유효하지 않을 경우, 해당 토큰 값과 접근하려는 사용자가 같지 않을 경우,
   *                           TODO: 로그아웃 사용자가 재발급 접근할 경우
   * @return TokenResponseDto: accessToken, refreshToken이 포함되어 있으며 각 1시간, 7일에 유효기간을 가진다.
   */
  @PostMapping("/reissue")
  public ResponseEntity<TokenResponseDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
    return ResponseEntity.ok(authService.reIssue(tokenRequestDto));
  }
}

package com.chs.cafeapp.auth.controller;

import com.chs.cafeapp.exception.CustomException;
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
   * @exception CustomException: 이미 존재하는 아이디, 닉네임일 경우, //TODO repassword도 추가하여 password값도 비교 추가
   * @return UserResponseDto: 아이디, 생성 날짜, 가입 축하 멘트
   */
  @PostMapping("/sing-up")
  public ResponseEntity<UserResponseDto> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
    return ResponseEntity.ok(authService.signUp(signUpRequestDto));
  }

  /**
   * 이메일 인증 Controller
   * @param request: 사용자의 이메일에서 링크를 누른 servletRequest 값
   * @exception CustomException: 이메일 인증기한(24시간)이 지났을 경우, 이미 인증이 끝난 사용자일 경우,
   *                              해당 uuid링크 값으로 유효한 사용자가 없을 경우 CustomException 발생
   * @return 해당 링크 페이지에서 json 형태로 UserResponse 값
   */
  @GetMapping("/email-auth")
  public ResponseEntity<UserResponseDto> emailAuth(HttpServletRequest request) {
    return ResponseEntity.ok(authService.emailAuth(request.getParameter("id")));
  }

  /**
   *  사용자(User)일반 로그인 Controller
   * @param signInRequestDto: 로그인 입력값 (username(loginId), password)
   * @exception CustomException: 로그인 아이디로 사용자가 존재하지 않을 경우, 비밀번호가 틀릴 경우,
   *                             이메일 인증을 진행하지 않았을 경우 CustomException 발생
   * @return TokenResponseDto: accessToken, refreshToken이 포함되어 있으며 각 1시간, 7일에 유효기간을 가진다.
   */
  @PostMapping("/sign-in")
  public ResponseEntity<TokenResponseDto> signin(@RequestBody SignInRequestDto signInRequestDto) {
    return ResponseEntity.ok(authService.signIn(signInRequestDto));
  }

  /**
   * 카페관계자(Admin) 일반 로그인 Controller
   * @param signInRequestDto: 로그인 입력값 (username(loginId), password)
   * @exception CustomException: 로그인 아이디로 사용자가 존재하지 않을 경우, 비밀번호가 틀릴 경우 CustomException 발생
   * @return TokenResponseDto:accessToken, refreshToken이 포함되어 있으며 각 1시간, 7일에 유효기간을 가진다.
   */
  @PostMapping("/admin/sign-in")
  public ResponseEntity<TokenResponseDto> adminSignIn(@RequestBody SignInRequestDto signInRequestDto) {
    return ResponseEntity.ok(authService.adminSignIn(signInRequestDto));
  }

  /**
   * access Token 재발급 Controller
   * @param tokenRequestDto: 기존에 로그인때 받았던 tokenResponseDto(accessToken, refreshToken)값
   * @exception CustomException: refreshToken이 우효하지 않을 경우, 해당 토큰 값과 접근하려는 사용자가 같지 않을 경우,
   *                           TODO: 로그아웃 사용자가 재발급 접근할 경우
   * @return TokenResponseDto: accessToken, refreshToken이 포함되어 있으며 각 1시간, 7일에 유효기간을 가진다.
   */
  @PostMapping("/reissue")
  public ResponseEntity<TokenResponseDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
    return ResponseEntity.ok(authService.reIssue(tokenRequestDto));
  }
}

package com.chs.cafeapp.auth.controller;

import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.member.dto.MemberSignUpRequestDto;
import com.chs.cafeapp.auth.service.impl.AuthMemberService;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.dto.SignInRequestDto;
import com.chs.cafeapp.auth.dto.AuthResponseDto;
import com.chs.cafeapp.global.mail.dto.EmailRequest;
import java.security.NoSuchAlgorithmException;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/members")
public class AuthMemberController {

  private final AuthMemberService authService;

  /**
   * 일반 회원가입 Controller
   * @param memberSignUpRequestDto: 일반 회원가입 입력 값
   * @exception CustomException: 이미 존재하는 아이디, 닉네임일 경우, repassword와 password값이 다를 경우
   * @return UserResponseDto: 아이디, 생성 날짜, 가입 축하 멘트
   */
  @PostMapping("/sign-up")
  public ResponseEntity<AuthResponseDto> signUp(@RequestBody MemberSignUpRequestDto memberSignUpRequestDto) throws MessagingException, NoSuchAlgorithmException {
    return ResponseEntity.ok(authService.signUp(memberSignUpRequestDto));
  }

  /**
   * 프론트가 존재하지 않을 때 이메일 인증 Controller
   * @param -: 사용자의 이메일에서 확인한 랜덤한 6자리 수
   * @exception CustomException : 이메일 인증기한(24시간)이 지났을 경우, 이미 인증이 끝난 사용자일 경우,
   *                              해당 랜덤한 6자리 수 값으로 유효한 사용자가 없을 경우 CustomException 발생
   * @return AuthResponseDto
   */
  @GetMapping("/email-auth")
  public ResponseEntity<AuthResponseDto> emailAuth(@RequestParam String id,
      @RequestParam String certifiedNumber) throws MessagingException {
    return ResponseEntity.ok(authService.emailAuth(id, certifiedNumber));
  }

  /**
   *  일반 사용자(Member)일반 로그인 Controller
   * @param signInRequestDto: 로그인 입력값 (username(loginId), password)
   * @exception CustomException: 로그인 아이디로 사용자가 존재하지 않을 경우, 비밀번호가 틀릴 경우,
   *                             이메일 인증을 진행하지 않았을 경우 CustomException 발생
   * @return TokenResponseDto: accessToken, refreshToken이 포함되어 있으며 각 1시간, 7일에 유효기간을 가진다.
   */
  @PostMapping("/sign-in")
  public ResponseEntity<TokenResponseDto> signIn(@RequestBody SignInRequestDto signInRequestDto) {
    return ResponseEntity.ok(authService.signIn(signInRequestDto));
  }

  /**
   * 일반 사용자 비밀번호 변경 Controller
   * @param accessToken
   * @param passwordEditInput 아이디, 기존 비밀번호, 새 비밀번호
   * @exception CustomException: 아이디로 일반 사용자가 존재하지 않을 경우, 기존 비밀번호가 틀렸을 경우,
   *                              해당 사용자의 token 값이 ROLE_ADMIN이 아닐 경우
   *                              해당 사용자의 권한이 2개 이상일 경우 CustomException 발생
   * @return PasswordEditResponse: 아이디, message(비밀번호가 변경되었습니다. or 비밀번호가 변경되지 않았습니다.)
       */
  @PostMapping("/passwords")
  public ResponseEntity<PasswordEditResponse> memberPassword(@RequestHeader("Authorization") String accessToken, @RequestBody PasswordEditInput passwordEditInput) {
    return ResponseEntity.ok(authService.changePassword(accessToken, passwordEditInput));
  }
}

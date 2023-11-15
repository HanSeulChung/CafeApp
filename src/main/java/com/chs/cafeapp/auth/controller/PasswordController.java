package com.chs.cafeapp.auth.controller;

import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.chs.cafeapp.exception.CustomException;

@RestController
@RequiredArgsConstructor
public class PasswordController {
  private final AuthService authService;

  /**
   * 카페 관계자 비밀번호 변경 Controller
   * @param passwordEditInput: 아이디, 기존 비밀번호, 새 비밀번호
   * @exception CustomException: 아이디로 카페관계자가 존재하지 않을 경우, 기존 비밀번호가 틀렸을 경우,
   *                              해당 사용자의 token 값이 ROLE_ADMIN이 아닐 경우
   *                              해당 사용자의 권한이 2개 이상일 경우 CustomException 발생
   * @return PasswordEditResponse: 아이디, message(비밀번호가 변경되었습니다. or 비밀번호가 변경되지 않았습니다.)
   */
  @PostMapping("/admin")
  public ResponseEntity<PasswordEditResponse> adminPassword(@RequestBody PasswordEditInput passwordEditInput) {
    return ResponseEntity.ok(authService.changePassword(passwordEditInput));
  }

  /**
   * 일반 사용자 비밀번호 변경 Controller
   * @param passwordEditInput: 아이디, 기존 비밀번호, 새 비밀번호
   * @exception CustomException: 아이디로 일반 사용자가 존재하지 않을 경우, 기존 비밀번호가 틀렸을 경우,
   *                              해당 사용자의 token 값이 ROLE_ADMIN이 아닐 경우
   *                              해당 사용자의 권한이 2개 이상일 경우 CustomException 발생
   * @return PasswordEditResponse: 아이디, message(비밀번호가 변경되었습니다. or 비밀번호가 변경되지 않았습니다.)
   */
  @PostMapping("/user")
  public ResponseEntity<PasswordEditResponse> userPassword(@RequestBody PasswordEditInput passwordEditInput) {
    return ResponseEntity.ok(authService.changePassword(passwordEditInput));
  }
}

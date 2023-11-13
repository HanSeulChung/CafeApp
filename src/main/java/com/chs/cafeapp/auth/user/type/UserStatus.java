package com.chs.cafeapp.auth.user.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

  /**
   * 가입 요청
   */
  USER_STATUS_REQ("가입 요청 중입니다."),

  /**
   * 이용 상태
   */
  USER_STATUS_ING("가입 완료 후 이용 중인 상태 입니다."),

  /**
   * 휴먼 상태
   */
  USER_STATUS_STOP("로그인 안한지 1년이 초과하여 휴먼 상태입니다."),

  /**
   * 탈퇴 상태
   */
  USER_STATUS_WITHDRAW("탈퇴 상태입니다.");

  private final String description;
}

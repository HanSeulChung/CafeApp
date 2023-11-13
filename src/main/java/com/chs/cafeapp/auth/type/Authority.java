package com.chs.cafeapp.auth.type;

public enum Authority {
  ROLE_YET_USER, // 메일 인증이 완료 되지 않은 서비스 가입자
  ROLE_USER, // 일반 카페 사용자
  ROLE_ADMIN // 카페 관계자
}

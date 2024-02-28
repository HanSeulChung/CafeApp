package com.chs.cafeapp.auth.type;

public enum Authority {
  ROLE_YET_MEMBER, // 메일 인증이 완료 되지 않은 서비스 가입자
  ROLE_MEMBER, // 일반 카페 사용자
  ROLE_YET_ADMIN,
  ROLE_ADMIN // 카페 관계자
}

package com.chs.cafeapp.auth.member.service;

import com.chs.cafeapp.auth.member.entity.Member;
import java.time.LocalDateTime;

public interface MemberService {

  /**
   * 사용자 로그인 날짜, 시간 저장
   */
  void updateLastLoginDateTime(String loginId, LocalDateTime localDateTime);

  /**
   * User 찾기
   */
  Member getMemberById(String loginId);
}

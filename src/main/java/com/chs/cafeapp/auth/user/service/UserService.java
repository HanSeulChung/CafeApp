package com.chs.cafeapp.auth.user.service;

import com.chs.cafeapp.auth.user.entity.User;
import java.time.LocalDateTime;

public interface UserService {

  /**
   * 사용자 로그인 날짜, 시간 저장
   */
  void updateLastLoginDateTime(String loginId, LocalDateTime localDateTime);

  /**
   * User 찾기
   */
  User getUserById(String loginId);
}

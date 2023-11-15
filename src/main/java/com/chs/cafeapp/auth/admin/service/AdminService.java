package com.chs.cafeapp.auth.admin.service;

import java.time.LocalDateTime;

public interface AdminService {

  /**
   * 카페 관계자 날짜 ,시간 업데이트
   */

  void updateLastLoginDateTime(String loginId, LocalDateTime localDateTime);
}

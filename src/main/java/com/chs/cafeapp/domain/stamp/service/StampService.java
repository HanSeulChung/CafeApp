package com.chs.cafeapp.domain.stamp.service;

import com.chs.cafeapp.domain.stamp.dto.StampDto;

public interface StampService {
  /**
   * 스탬프 조회
   */
  StampDto viewStamp(String userId);

  /**
   * 스탬프 추가
   */
  StampDto addStampNumbers(long stampNumbers, String userId);
}

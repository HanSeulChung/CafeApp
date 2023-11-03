package com.chs.cafeapp.coupon.service;

import com.chs.cafeapp.coupon.dto.CouponDto;

public interface CouponService {

  /**
   * 쿠폰 생성
   */
  CouponDto createCouponByStamp(String userId);
}

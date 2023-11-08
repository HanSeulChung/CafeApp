package com.chs.cafeapp.coupon.service;

import com.chs.cafeapp.coupon.dto.CouponDto;
import com.chs.cafeapp.coupon.dto.CouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {

  /**
   * 쿠폰 생성
   */
  CouponDto createCouponByStamp(String userId);

  /**
   * 쿠폰 전체 조회
   */
  Page<CouponResponse> viewAllCoupons(String userId, Pageable pageable);
  /**
   * 사용할 수 있는 쿠폰만 조회
   */
  Page<CouponResponse> viewAllCanUseCoupons(String userId, Pageable pageable);
  /**
   * 사용할 수 없는 쿠폰 전체 조회
   */
  Page<CouponResponse> viewAllCanNotUseCoupons(String userId, Pageable pageable);
}

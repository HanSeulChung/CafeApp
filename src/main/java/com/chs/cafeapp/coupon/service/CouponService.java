package com.chs.cafeapp.coupon.service;

import com.chs.cafeapp.coupon.dto.CouponDto;
import com.chs.cafeapp.coupon.dto.CouponResponse;
import java.util.List;

public interface CouponService {

  /**
   * 쿠폰 생성
   */
  CouponDto createCouponByStamp(String userId);

  /**
   * 쿠폰 전체 조회
   */
  List<CouponResponse> viewAllCoupons(String userId);
  /**
   * 사용할 수 있는 쿠폰만 조회
   */
  List<CouponResponse> viewAllCanUseCoupons(String userId);
  /**
   * 사용할 수 없는 쿠폰 전체 조회
   */
  List<CouponResponse> viewAllCanNotUseCoupons(String userId);
}

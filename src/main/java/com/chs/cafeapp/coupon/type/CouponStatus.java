package com.chs.cafeapp.coupon.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponStatus {
  /**
   * 사용 가능
   */
  CAN_USE_COUPON("사용 가능"),
  /**
   * 기간 만료(사용 불가)
   */
  EXPIRED_COUPON("기간 만료"),
  /**
   * 사용 완료(사용 불가)
   */
  ALREADY_USED_COUPON("사용 완료");

  private final String statusName;
}

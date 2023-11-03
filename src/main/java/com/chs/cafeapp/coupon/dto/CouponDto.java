package com.chs.cafeapp.coupon.dto;

import com.chs.cafeapp.coupon.entity.Coupon;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CouponDto {
  private long id;
  private String userId;
  private String couponName;
  private int price;
  private boolean usedYn;
  private LocalDateTime expirationDateTime;
  private boolean expiredYn;

  public static CouponDto of(Coupon coupon) {
    return CouponDto.builder()
        .id(coupon.getId())
        .userId(coupon.getUser().getLoginId())
        .couponName(coupon.getCouponName())
        .price(coupon.getPrice())
        .usedYn(coupon.isUsedYn())
        .expirationDateTime(coupon.getExpirationDateTime())
        .expiredYn(coupon.isExpiredYn())
        .build();
  }
}

package com.chs.cafeapp.coupon.dto;

import com.chs.cafeapp.coupon.entity.Coupon;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class CouponDto {
  private long id;
  private String userId;
  private String couponName;
  private int price;
  private boolean usedYn;
  private LocalDateTime expirationDateTime;
  private boolean expiredYn;

  public static List<CouponDto> convertListDtoFromPageEntity(Page<Coupon> coupons) {
    List<Coupon> couponList = coupons.getContent();

    if (couponList == null) {
      return new ArrayList<>();
    }

    return couponList.stream()
        .map(CouponDto::of)
        .collect(Collectors.toList());
  }
  public static List<CouponDto> of(List<Coupon> couponList) {
    if (couponList != null) {
      List<CouponDto> couponDtoList = new ArrayList<>();
      for (Coupon coupon : couponList) {
        couponDtoList.add(CouponDto.of(coupon));
      }
      return couponDtoList;
    }
    return new ArrayList<>();
  }
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

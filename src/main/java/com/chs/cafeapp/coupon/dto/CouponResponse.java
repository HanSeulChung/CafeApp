package com.chs.cafeapp.coupon.dto;

import static com.chs.cafeapp.coupon.type.CouponStatus.ALREADY_USED_COUPON;
import static com.chs.cafeapp.coupon.type.CouponStatus.CAN_USE_COUPON;
import static com.chs.cafeapp.coupon.type.CouponStatus.EXPIRED_COUPON;

import com.chs.cafeapp.coupon.type.CouponStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CouponResponse {

  private long id;
  private String userId;
  private String couponName;
  private int price;
  private boolean usedYn;
  private LocalDateTime expirationDateTime;
  private boolean expiredYn;
  private String couponStatus;

  public void setCouponStatus(String couponStatus) {
    this.couponStatus = couponStatus;
  }

  public static List<CouponResponse> toResponse(List<CouponDto> couponDtoList) {

    if (couponDtoList != null) {
      List<CouponResponse> couponResponseList = new ArrayList<>();
      for (CouponDto couponDto : couponDtoList) {
        CouponResponse couponResponse = CouponResponse.toResponse(couponDto);
        if (!couponResponse.usedYn && !couponResponse.expiredYn) {
          couponResponse.setCouponStatus(CAN_USE_COUPON.getStatusName());
        }
        if (couponResponse.usedYn) {
          couponResponse.setCouponStatus(ALREADY_USED_COUPON.getStatusName());
        }
        if (couponResponse.expiredYn) {
          couponResponse.setCouponStatus(EXPIRED_COUPON.getStatusName());
        }
        couponResponseList.add(couponResponse);
      }
      return couponResponseList;
    }
    return new ArrayList<>();
  }
  public static CouponResponse toResponse(CouponDto couponDto) {
    return CouponResponse.builder()
        .id(couponDto.getId())
        .userId(couponDto.getUserId())
        .couponName(couponDto.getCouponName())
        .price(couponDto.getPrice())
        .usedYn(couponDto.isUsedYn())
        .expirationDateTime(couponDto.getExpirationDateTime())
        .expiredYn(couponDto.isExpiredYn())
        .build();
  }

  public static List<CouponResponse> toResponse(List<CouponDto> couponDtoList, String couponStatus) {

    if (couponDtoList != null) {
      List<CouponResponse> couponResponseList = new ArrayList<>();
      for (CouponDto couponDto : couponDtoList) {
        couponResponseList.add(CouponResponse.toResponse(couponDto, couponStatus));
      }
      return couponResponseList;
    }
    return new ArrayList<>();
  }
  public static CouponResponse toResponse(CouponDto couponDto, String couponStatus) {
    return CouponResponse.builder()
        .id(couponDto.getId())
        .userId(couponDto.getUserId())
        .couponName(couponDto.getCouponName())
        .price(couponDto.getPrice())
        .usedYn(couponDto.isUsedYn())
        .expirationDateTime(couponDto.getExpirationDateTime())
        .expiredYn(couponDto.isExpiredYn())
        .couponStatus(couponStatus)
        .build();
  }
}

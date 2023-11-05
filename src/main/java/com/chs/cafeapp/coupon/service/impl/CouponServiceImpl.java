package com.chs.cafeapp.coupon.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.COUPON_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_USER_AND_COUPON;
import static com.chs.cafeapp.exception.type.ErrorCode.USER_NOT_FOUND;

import com.chs.cafeapp.coupon.dto.CouponDto;
import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.coupon.repository.CouponRepository;
import com.chs.cafeapp.coupon.service.CouponService;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
  private final UserRepository userRepository;
  private final CouponRepository couponRepository;

  private final String COUPON_NAME_BY_STAMP = "스탬프 10회 적립 아메리카노 교환 쿠폰";
  private final int COUPON_PRICE_BY_STAMP = 4100;
  public Coupon validationUserAndCoupon(String userId, long couponId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CustomException(COUPON_NOT_FOUND));

    if (!coupon.getUser().getLoginId().equals(userId)) {
      throw new CustomException(NOT_MATCH_USER_AND_COUPON);
    }
    return coupon;
  }

  @Override
  public CouponDto createCouponByStamp(String userId) {
    User user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));


    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expirationDateTime = now.plusMonths(2);

    Coupon couponByStamp = Coupon.builder()
        .couponName(COUPON_NAME_BY_STAMP)
        .price(COUPON_PRICE_BY_STAMP)
        .usedYn(false)
        .user(user)
        .expirationDateTime(expirationDateTime)
        .expiredYn(false)
        .build();

    user.setCoupons(couponByStamp);
    userRepository.save(user);

    return CouponDto.of(couponRepository.save(couponByStamp));
  }
}

package com.chs.cafeapp.coupon.repository;

import com.chs.cafeapp.coupon.entity.Coupon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

  List<Coupon> findAllByUserId(Long userId);

  List<Coupon> findAllByUserIdAndUsedYnFalseAndExpiredYnFalse(Long userId);

  List<Coupon> findAllByUserIdAndUsedYnTrueOrUserIdAndExpiredYnTrue(Long userId1, Long userId2);
}

package com.chs.cafeapp.coupon.repository;

import com.chs.cafeapp.coupon.entity.Coupon;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

  List<Coupon> findAllByUserId(Long userId);
  List<Coupon> findAllByUpdateDateTimeLessThan(LocalDateTime localDateTime);
  List<Coupon> findAllByUserIdAndUsedYnFalseAndExpiredYnFalse(Long userId);
  List<Coupon> findAllByUserIdAndUsedYnTrueOrUserIdAndExpiredYnTrue(Long userId1, Long userId2);

  Page<Coupon> findAllByUserId(Long userId, Pageable pageable);
  Page<Coupon> findAllByUserIdAndUsedYnFalseAndExpiredYnFalse(Long userId, Pageable pageable);
  Page<Coupon> findAllByUserIdAndUsedYnTrueOrUserIdAndExpiredYnTrue(Long userId1, Long userId2, Pageable pageable);

  @Transactional
  void deleteAllByUpdateDateTimeLessThan(LocalDateTime nowLocalDateTime);
}

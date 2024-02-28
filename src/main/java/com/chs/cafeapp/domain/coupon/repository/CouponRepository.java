package com.chs.cafeapp.domain.coupon.repository;

import com.chs.cafeapp.domain.coupon.entity.Coupon;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

  List<Coupon> findAllByMemberId(Long memberId);
  List<Coupon> findAllByUpdateDateTimeLessThan(LocalDateTime localDateTime);
  List<Coupon> findAllByMemberIdAndUsedYnFalseAndExpiredYnFalse(Long memberId);
  List<Coupon> findAllByMemberIdAndUsedYnTrueOrMemberIdAndExpiredYnTrue(Long memberId1, Long memberId2);

  Page<Coupon> findAllByMemberId(Long memberId, Pageable pageable);
  Page<Coupon> findAllByMemberIdAndUsedYnFalseAndExpiredYnFalse(Long memberId, Pageable pageable);
  Page<Coupon> findAllByMemberIdAndUsedYnTrueOrMemberIdAndExpiredYnTrue(Long memberId1, Long memberId2, Pageable pageable);

  @Transactional
  void deleteAllByUpdateDateTimeLessThan(LocalDateTime nowLocalDateTime);
}

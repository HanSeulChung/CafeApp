package com.chs.cafeapp.coupon.repository;

import com.chs.cafeapp.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}

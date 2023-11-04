package com.chs.cafeapp.coupon.controller;

import com.chs.cafeapp.coupon.dto.CouponResponse;
import com.chs.cafeapp.coupon.service.CouponService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 쿠폰 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {
  private final CouponService couponService;

  @GetMapping()
  public ResponseEntity<List<CouponResponse>> viewAllCoupons(@RequestParam String userId) {
    return ResponseEntity.ok(couponService.viewAllCoupons(userId));
  }

  @GetMapping("/usable")
  public ResponseEntity<List<CouponResponse>> viewAllCanUseCoupons(@RequestParam String userId) {
    return ResponseEntity.ok(couponService.viewAllCanUseCoupons(userId));
  }

  @GetMapping("/un-usable")
  public ResponseEntity<List<CouponResponse>> viewAllCanNotUseCoupons(@RequestParam String userId) {
    return ResponseEntity.ok(couponService.viewAllCanNotUseCoupons(userId));
  }
}

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

  /**
   * 쿠폰 전체 조회 Controller
   * @param userId: 쿠폰 조회할 사용자의 id
   * @return List<CouponResponse>: 사용자가 가지고 있는 모든 쿠폰 List 반환
   *                                기존 CouponDto에서 사용 가능, 사용 완료, 기간 만료 등의 statusName 추가
   */
  @GetMapping()
  public ResponseEntity<List<CouponResponse>> viewAllCoupons(@RequestParam String userId) {
    return ResponseEntity.ok(couponService.viewAllCoupons(userId));
  }

  /**
   * 사용 가능한 쿠폰 전체 조회 Controller
   * @param userId: 쿠폰 조회할 사용자의 id
   * @return List<CouponResponse>: 사용자가 가지고 있는 모든 쿠폰 중 사용 가능한 쿠폰 List 반환
   *                                기존 CouponDto에서 사용 가능, 사용 완료, 기간 만료 등의 statusName 추가
   */
  @GetMapping("/usable")
  public ResponseEntity<List<CouponResponse>> viewAllCanUseCoupons(@RequestParam String userId) {
    return ResponseEntity.ok(couponService.viewAllCanUseCoupons(userId));
  }

  /**
   * 사용 불가능한 쿠폰 전체 조회 Controller
   * @param userId: 쿠폰 조회할 사용자의 id
   * @return List<CouponResponse>: 사용자가 가지고 있는 모든 쿠폰 중 사용 불가능한 쿠폰 List 반환
   *                                기존 CouponDto에서 사용 가능, 사용 완료, 기간 만료 등의 statusName 추가
   */
  @GetMapping("/un-usable")
  public ResponseEntity<List<CouponResponse>> viewAllCanNotUseCoupons(@RequestParam String userId) {
    return ResponseEntity.ok(couponService.viewAllCanNotUseCoupons(userId));
  }
}

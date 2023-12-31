package com.chs.cafeapp.coupon.controller;

import com.chs.cafeapp.coupon.dto.CouponResponse;
import com.chs.cafeapp.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
   * @return Page<CouponResponse>: 사용자가 가지고 있는 모든 쿠폰 Page 반환
   *                                기존 CouponDto에서 사용 가능, 사용 완료, 기간 만료 등의 statusName 추가
   */
  @GetMapping()
  public ResponseEntity<Page<CouponResponse>> viewAllCoupons(
      @RequestParam("userId") String userId,
      Pageable pageable) {
    return ResponseEntity.ok(couponService.viewAllCoupons(userId, pageable));
  }

  /**
   * 사용 가능한 쿠폰 전체 조회 Controller
   * @param userId: 쿠폰 조회할 사용자의 id
   * @return Page<CouponResponse>: 사용자가 가지고 있는 모든 쿠폰 중 사용 가능한 쿠폰 Page 반환
   *                                기존 CouponDto에서 사용 가능, 사용 완료, 기간 만료 등의 statusName 추가
   */
  @GetMapping("/usable-coupons")
  public ResponseEntity<Page<CouponResponse>> viewAllCanUseCoupons(
      @RequestParam("userId") String userId,
      Pageable pageable) {
    return ResponseEntity.ok(couponService.viewAllCanUseCoupons(userId, pageable));
  }

  /**
   * 사용 불가능한 쿠폰 전체 조회 Controller
   * @param userId: 쿠폰 조회할 사용자의 id
   * @return Page<CouponResponse>: 사용자가 가지고 있는 모든 쿠폰 중 사용 불가능한 쿠폰 Page 반환
   *                                기존 CouponDto에서 사용 가능, 사용 완료, 기간 만료 등의 statusName 추가
   */
  @GetMapping("/un-usable")
  public ResponseEntity<Page<CouponResponse>> viewAllCanNotUseCoupons(
      @RequestParam("userId") String userId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(couponService.viewAllCanNotUseCoupons(userId, pageable));
  }
}

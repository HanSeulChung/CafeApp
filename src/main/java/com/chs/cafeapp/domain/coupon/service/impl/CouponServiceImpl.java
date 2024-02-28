package com.chs.cafeapp.domain.coupon.service.impl;

import static com.chs.cafeapp.global.exception.type.ErrorCode.COUPON_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_MEMBER_AND_COUPON;
import static com.chs.cafeapp.global.exception.type.ErrorCode.MEMBER_NOT_FOUND;

import com.chs.cafeapp.domain.coupon.dto.CouponDto;
import com.chs.cafeapp.domain.coupon.dto.CouponResponse;
import com.chs.cafeapp.domain.coupon.entity.Coupon;
import com.chs.cafeapp.domain.coupon.repository.CouponRepository;
import com.chs.cafeapp.domain.coupon.service.CouponService;
import com.chs.cafeapp.domain.coupon.type.CouponStatus;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
  private final MemberRepository memberRepository;
  private final CouponRepository couponRepository;

  private final String COUPON_NAME_BY_STAMP = "스탬프 10회 적립 아메리카노 교환 쿠폰";
  private final int COUPON_PRICE_BY_STAMP = 4100;

  public Member validationMember(String memberId) {
    Member member = memberRepository.findByLoginId(memberId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    return member;
  }
  public Coupon validationMemberAndCoupon(String memberId, long couponId) {
    Member member = memberRepository.findByLoginId(memberId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CustomException(COUPON_NOT_FOUND));

    if (!coupon.getMember().getLoginId().equals(memberId)) {
      throw new CustomException(NOT_MATCH_MEMBER_AND_COUPON);
    }
    return coupon;
  }

  @Override
  public CouponDto createCouponByStamp(String memberId) {
    Member member = memberRepository.findByLoginId(memberId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));


    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expirationDateTime = now.plusMonths(2);

    Coupon couponByStamp = Coupon.builder()
        .couponName(COUPON_NAME_BY_STAMP)
        .price(COUPON_PRICE_BY_STAMP)
        .usedYn(false)
        .member(member)
        .expirationDateTime(expirationDateTime)
        .expiredYn(false)
        .build();

    Coupon saveCoupon = couponRepository.save(couponByStamp);
    member.setCoupons(saveCoupon);
    memberRepository.save(member);

    return CouponDto.of(saveCoupon);
  }

  @Override
  public Page<CouponResponse> viewAllCoupons(String memberId, Pageable pageable) {
    Member member = validationMember(memberId);
    Page<Coupon> coupons = couponRepository.findAllByMemberId(member.getId(), pageable);
    List<CouponDto> couponDtoList = CouponDto.convertListDtoFromPageEntity(coupons);
    List<CouponResponse> responseList = CouponResponse.toResponse(couponDtoList);
    return new PageImpl<>(responseList, coupons.getPageable(), coupons.getTotalElements());
  }

  @Override
  public Page<CouponResponse> viewAllCanUseCoupons(String memberId, Pageable pageable) {
    Member member = validationMember(memberId);
    Page<Coupon> coupons = couponRepository.findAllByMemberIdAndUsedYnFalseAndExpiredYnFalse(member.getId(), pageable);
    List<CouponDto> couponDtoList = CouponDto.convertListDtoFromPageEntity(coupons);
    List<CouponResponse> responseList = CouponResponse.toResponse(couponDtoList,  CouponStatus.CAN_USE_COUPON.getStatusName());
    return new PageImpl<>(responseList, coupons.getPageable(), coupons.getTotalElements());
  }

  @Override
  public Page<CouponResponse> viewAllCanNotUseCoupons(String memberId, Pageable pageable) {
    Member member = validationMember(memberId);
    Page<Coupon> coupons = couponRepository.findAllByMemberIdAndUsedYnTrueOrMemberIdAndExpiredYnTrue(member.getId(), member.getId(), pageable);
    List<CouponDto> couponDtoList = CouponDto.convertListDtoFromPageEntity(coupons);
    List<CouponResponse> responseList = CouponResponse.toResponse(couponDtoList);
    return new PageImpl<>(responseList, coupons.getPageable(), coupons.getTotalElements());
  }
}

package com.chs.cafeapp.service.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.chs.cafeapp.domain.coupon.dto.CouponDto;
import com.chs.cafeapp.domain.coupon.dto.CouponResponse;
import com.chs.cafeapp.domain.coupon.entity.Coupon;
import com.chs.cafeapp.domain.coupon.repository.CouponRepository;
import com.chs.cafeapp.domain.coupon.service.impl.CouponServiceImpl;
import com.chs.cafeapp.domain.stamp.entity.Stamp;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {
  static Pageable pageable;
  static Member member;
  static Coupon coupon1;
  static Coupon coupon2;
  static Coupon coupon3;
  static Coupon coupon4;
  static Coupon coupon5;
  @Mock
  private MemberRepository userRepository;

  @Mock
  private CouponRepository couponRepository;

  @InjectMocks
  private CouponServiceImpl couponService;

  @BeforeEach
  public void setCouponAndUser() {
    coupon1 = Coupon.builder()
        .id(1L)
        .usedYn(false)
        .expiredYn(false)
        .build();

    coupon2 = Coupon.builder()
        .id(2L)
        .usedYn(false)
        .expiredYn(false)
        .build();

    coupon3 = Coupon.builder()
        .id(3L)
        .usedYn(true)
        .expiredYn(false)
        .build();

    coupon4 = Coupon.builder()
        .id(4L)
        .usedYn(true)
        .expiredYn(true)
        .build();

    coupon5 = Coupon.builder()
        .id(5L)
        .usedYn(false)
        .expiredYn(true)
        .build();

    member = Member.builder()
        .id(1L)
        .loginId("user2@naver.com")
        .coupons(Arrays.asList(coupon1, coupon2, coupon3, coupon4, coupon5))
        .build();

    coupon1.setMember(member);
    coupon2.setMember(member);
    coupon3.setMember(member);
    coupon4.setMember(member);
    coupon5.setMember(member);

    userRepository.save(member);

    couponRepository.save(coupon1);
    couponRepository.save(coupon2);
    couponRepository.save(coupon3);
    couponRepository.save(coupon4);
    couponRepository.save(coupon5);

    pageable = new Pageable() {
      @Override
      public int getPageNumber() {
        return 0;
      }

      @Override
      public int getPageSize() {
        return 0;
      }

      @Override
      public long getOffset() {
        return 0;
      }

      @Override
      public Sort getSort() {
        return null;
      }

      @Override
      public Pageable next() {
        return null;
      }

      @Override
      public Pageable previousOrFirst() {
        return null;
      }

      @Override
      public Pageable first() {
        return null;
      }

      @Override
      public boolean hasPrevious() {
        return false;
      }
    };
  }

  @Test
  @DisplayName("스탬프 10회 적립시 쿠폰 자동 생성 성공")
  void createCouponByStamp_Success() {
    // given
    Member user3 = Member.builder()
        .id(3L)
        .loginId("user3@naver.com")
        .build();

    Stamp stamp = Stamp.builder()
        .id(1L)
        .stampNumbers(8)
        .member(user3)
        .build();

    LocalDateTime expirationDateTime = LocalDateTime.now().plusMonths(2);
    Coupon coupon = Coupon.builder()
        .id(100L)
        .member(user3)
        .price(4100)
        .expirationDateTime(expirationDateTime)
        .build();

    when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(user3));
    when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
    // when
    CouponDto couponDtoByStamp = couponService.createCouponByStamp("user3@naver.com");

    // then
    assertNotNull(couponDtoByStamp);
    assertTrue(couponDtoByStamp.getExpirationDateTime().equals(expirationDateTime));
    assertEquals(couponDtoByStamp.getPrice(), 4100);
  }
  @Test
  @DisplayName("쿠폰 전체 조회")
  void viewAllCouponsTest() {
      //given

      //when
    when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(member));
    when(couponRepository.findAllByMemberId(1L)).thenReturn(Arrays.asList(coupon1, coupon2, coupon3, coupon4, coupon5));
    Page<CouponResponse> couponResponsePage = couponService.viewAllCoupons("user2@naver.com", pageable);

    //then
    assertNotNull(couponResponsePage);
    assertEquals(couponResponsePage.getTotalElements(), 5);
  }

  @Test
  @DisplayName("사용 가능한 쿠폰 전체 조회")
  void viewAllCanUseCouponsTest() {
      //given

    //when
    when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(member));
    when(couponRepository.findAllByMemberIdAndUsedYnFalseAndExpiredYnFalse(1L)).thenReturn(Arrays.asList(coupon1, coupon2));
    Page<CouponResponse> couponResponsePage = couponService.viewAllCanUseCoupons("user2@naver.com", pageable);

    //then
    assertNotNull(couponResponsePage);
    assertEquals(couponResponsePage.getTotalElements(), 2);
//    assertEquals(couponResponsePage.get(0).map(CouponResponse::getCouponStatus), CAN_USE_COUPON.getStatusName());
//    assertEquals(couponResponsePage.get(1).getCouponStatus(), CAN_USE_COUPON.getStatusName());
  }

  @Test
  @DisplayName("사용 불가능한 쿠폰 전체 조회")
  void viewAllCanNotUsedCouponsTest() {
    //given

    //when
    when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(member));
    when(couponRepository.findAllByMemberIdAndUsedYnTrueOrMemberIdAndExpiredYnTrue(1L, 1L)).thenReturn(Arrays.asList(coupon3, coupon4, coupon5));
    Page<CouponResponse> couponResponsePage = couponService.viewAllCanNotUseCoupons("user2@naver.com", pageable);

    //then
    assertNotNull(couponResponsePage);
    assertEquals(couponResponsePage.getTotalElements(), 3);
//    assertEquals(couponResponsePage.get(0).getCouponStatus(), ALREADY_USED_COUPON.getStatusName());
//    assertEquals(couponResponsePage.get(1).getCouponStatus(), ALREADY_USED_COUPON.getStatusName());
//    assertEquals(couponResponsePage.get(2).getCouponStatus(), EXPIRED_COUPON.getStatusName());
  }
}
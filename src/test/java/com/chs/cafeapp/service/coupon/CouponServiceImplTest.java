package com.chs.cafeapp.service.coupon;

import static com.chs.cafeapp.coupon.type.CouponStatus.ALREADY_USED_COUPON;
import static com.chs.cafeapp.coupon.type.CouponStatus.CAN_USE_COUPON;
import static com.chs.cafeapp.coupon.type.CouponStatus.EXPIRED_COUPON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.chs.cafeapp.coupon.dto.CouponDto;
import com.chs.cafeapp.coupon.dto.CouponResponse;
import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.coupon.repository.CouponRepository;
import com.chs.cafeapp.coupon.service.impl.CouponServiceImpl;
import com.chs.cafeapp.stamp.entity.Stamp;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {
  static User user;
  static Coupon coupon1;
  static Coupon coupon2;
  static Coupon coupon3;
  static Coupon coupon4;
  static Coupon coupon5;
  @Mock
  private UserRepository userRepository;

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

    user = User.builder()
        .id(1L)
        .loginId("user2@naver.com")
        .coupons(Arrays.asList(coupon1, coupon2, coupon3, coupon4, coupon5))
        .build();

    coupon1.setUser(user);
    coupon2.setUser(user);
    coupon3.setUser(user);
    coupon4.setUser(user);
    coupon5.setUser(user);

    userRepository.save(user);

    couponRepository.save(coupon1);
    couponRepository.save(coupon2);
    couponRepository.save(coupon3);
    couponRepository.save(coupon4);
    couponRepository.save(coupon5);
  }

  @Test
  @DisplayName("스탬프 10회 적립시 쿠폰 자동 생성 성공")
  void createCouponByStamp_Success() {
    // given
    User user3 = User.builder()
        .id(3L)
        .loginId("user3@naver.com")
        .build();

    Stamp stamp = Stamp.builder()
        .id(1L)
        .stampNumbers(8)
        .user(user3)
        .build();

    LocalDateTime expirationDateTime = LocalDateTime.now().plusMonths(2);
    Coupon coupon = Coupon.builder()
        .id(100L)
        .user(user3)
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
    when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(user));
    when(couponRepository.findAllByUserId(1L)).thenReturn(Arrays.asList(coupon1, coupon2, coupon3, coupon4, coupon5));
    List<CouponResponse> couponResponseList = couponService.viewAllCoupons("user2@naver.com");

    //then
    assertNotNull(couponResponseList);
    assertEquals(couponResponseList.size(), 5);
  }

  @Test
  @DisplayName("사용 가능한 쿠폰 전체 조회")
  void viewAllCanUseCouponsTest() {
      //given

    //when
    when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(user));
    when(couponRepository.findAllByUserIdAndUsedYnFalseAndExpiredYnFalse(1L)).thenReturn(Arrays.asList(coupon1, coupon2));
    List<CouponResponse> couponResponseList = couponService.viewAllCanUseCoupons("user2@naver.com");

    //then
    assertNotNull(couponResponseList);
    assertEquals(couponResponseList.size(), 2);
    assertEquals(couponResponseList.get(0).getCouponStatus(), CAN_USE_COUPON.getStatusName());
    assertEquals(couponResponseList.get(1).getCouponStatus(), CAN_USE_COUPON.getStatusName());
  }

  @Test
  @DisplayName("사용 불가능한 쿠폰 전체 조회")
  void viewAllCanNotUsedCouponsTest() {
    //given

    //when
    when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(user));
    when(couponRepository.findAllByUserIdAndUsedYnTrueOrUserIdAndExpiredYnTrue(1L, 1L)).thenReturn(Arrays.asList(coupon3, coupon4, coupon5));
    List<CouponResponse> couponResponseList = couponService.viewAllCanNotUseCoupons("user2@naver.com");

    //then
    assertNotNull(couponResponseList);
    assertEquals(couponResponseList.size(), 3);
    assertEquals(couponResponseList.get(0).getCouponStatus(), ALREADY_USED_COUPON.getStatusName());
    assertEquals(couponResponseList.get(1).getCouponStatus(), ALREADY_USED_COUPON.getStatusName());
    assertEquals(couponResponseList.get(2).getCouponStatus(), EXPIRED_COUPON.getStatusName());
  }
}
package com.chs.cafeapp.service.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.chs.cafeapp.coupon.dto.CouponDto;
import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.coupon.repository.CouponRepository;
import com.chs.cafeapp.coupon.service.impl.CouponServiceImpl;
import com.chs.cafeapp.stamp.entity.Stamp;
import com.chs.cafeapp.user.entity.User;
import com.chs.cafeapp.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private CouponRepository couponRepository;

  @InjectMocks
  private CouponServiceImpl couponService;

  @Test
  @DisplayName("스탬프 10회 적립시 쿠폰 자동 생성 성공")
  void createCouponByStamp_Success() {
    // given
    User user = User.builder()
        .id(1L)
        .loginId("user2@naver.com")
        .build();

    Stamp stamp = Stamp.builder()
        .id(1L)
        .stampNumbers(8)
        .user(user)
        .build();

    LocalDateTime expirationDateTime = LocalDateTime.now().plusMonths(2);
    Coupon coupon = Coupon.builder()
        .id(1L)
        .user(user)
        .price(4100)
        .expirationDateTime(expirationDateTime)
        .build();

    when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(user));
    when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
    // when
    CouponDto couponDtoByStamp = couponService.createCouponByStamp("user2@naver.com");

    // then
    assertNotNull(couponDtoByStamp);
    assertTrue(couponDtoByStamp.getExpirationDateTime().equals(expirationDateTime));
    assertEquals(couponDtoByStamp.getPrice(), 4100);
  }
}
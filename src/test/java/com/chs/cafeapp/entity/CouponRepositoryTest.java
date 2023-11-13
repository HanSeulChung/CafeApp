package com.chs.cafeapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.coupon.repository.CouponRepository;
import com.chs.cafeapp.auth.user.entity.User;
import com.chs.cafeapp.auth.user.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CouponRepositoryTest {
  static User user;
  static Coupon coupon1;
  static Coupon coupon2;
  static Coupon coupon3;
  static Coupon coupon4;
  static Coupon coupon5;
  @Autowired
  private EntityManager em;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CouponRepository couponRepository;

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

    em.merge(coupon1);
    em.merge(coupon2);
    em.merge(coupon3);
    em.merge(coupon4);
    em.merge(coupon5);

    user = User.builder()
        .id(1L)
        .loginId("user2@naver.com")
        .coupons(Arrays.asList(coupon1, coupon2, coupon3, coupon4, coupon5))
        .build();

    em.merge(user);

    coupon1.setUser(user);
    coupon2.setUser(user);
    coupon3.setUser(user);
    coupon4.setUser(user);
    coupon5.setUser(user);

    em.merge(coupon1);
    em.merge(coupon2);
    em.merge(coupon3);
    em.merge(coupon4);
    em.merge(coupon5);
  }

  @Test
  @DisplayName("쿠폰 전체 조회")
  void findAllByUserId() {

    // when
    List<Coupon> coupons = couponRepository.findAllByUserId(1L);
    // then
    assertNotNull(coupons);
    assertEquals(coupons.size(), 5);
  }

  @Test
  @DisplayName("사용 가능한 쿠폰 전체 조회")
  void findAllByUserIdAndUsedYnFalseAndExpiredYnFalse() {
    // when
    List<Coupon> coupons = couponRepository.findAllByUserIdAndUsedYnFalseAndExpiredYnFalse(1L);
    // then
    assertNotNull(coupons);
    assertEquals(coupons.size(), 2);
  }

  @Test
  @DisplayName("사용 불가능한 쿠폰 전체 조회")
  void findAllByUserIdAndUsedYnTrueOrUserIdAndExpiredYnTrue() {
    // when
    List<Coupon> coupons = couponRepository.findAllByUserIdAndUsedYnTrueOrUserIdAndExpiredYnTrue(1L, 1L);
    // then
    assertNotNull(coupons);
    assertEquals(coupons.size(), 3);
  }
}
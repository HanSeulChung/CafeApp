package com.chs.cafeapp.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import com.chs.cafeapp.coupon.entity.Coupon;
import com.chs.cafeapp.coupon.repository.CouponRepository;
import com.chs.cafeapp.order.entity.Order;
import com.chs.cafeapp.order.repository.OrderRepository;
import com.chs.cafeapp.order.type.OrderStatus;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DataBaseDeleteSchedulerTest {
  @Autowired
  private Clock clock;

  @Autowired
  private MemberRepository userRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private DataBaseDeleteScheduler dataBaseDeleteScheduler;

  @Autowired
  private EntityManager em;

  @BeforeEach
  void setUp() {
    Instant fixedInstant = Instant.parse("2023-11-01T12:00:00Z");
    clock.fixed(fixedInstant, ZoneOffset.UTC);

    Order order = Order.builder()
        .id(2L)
        .orderStatus(OrderStatus.PayFail)
        .totalPrice(4100)
        .build();
    em.merge(order);


    Coupon coupon = Coupon.builder()
        .id(2L)
        .expiredYn(false)
        .price(4100)
        .build();

    em.merge(coupon);

    Member user = Member.builder()
        .id(2L)
        .userName("신생사용자 이름")
        .build();

    em.merge(user);
  }

  // TODO: test database를 따로 h2-database로 두어 테스트 코드 진행하고 싶었으나 진행하지 못함.
//  @Test
//  @DisplayName("UpdateDateTime이 1년 초과된 주문 삭제 성공")
//  void orderAutoDeleteScheduling() {
//    Instant fixedInstant = Instant.parse("2021-11-01T12:00:00Z");
//    clock.fixed(fixedInstant, ZoneOffset.UTC);
//    LocalDateTime now = LocalDateTime.now();
//    LocalDateTime oneYearAgo = now.minusYears(1);
//
//    Order order1 = Order.builder()
//        .id(1L)
//        .orderStatus(OrderStatus.PayFail)
//        .totalPrice(4100)
//        .build();
//    em.merge(order1);
//
//    // 특정 시간 이전 주문 삭제 스케줄러 메서드 호출
////    dataBaseDeleteScheduler.orderAutoDeleteScheduling();
//    em.clear();
//    // orderRepository에서 deleteAllByUpdateDateTimeLessThan 메서드가 1번 호출되는지 확인
//    List<Order> remainingOrders = orderRepository.findAll();
//    assertThat(remainingOrders).hasSize(2);
//  }
//
//  @Test
//  @DisplayName("UpdateDateTime이 1년 초과된 쿠폰 삭제 성공")
//  void couponAutoDeleteScheduling() {
//
//  }
//
//  @Test
//  @DisplayName("UpdateDateTime이 2년 초과된 쿠폰 삭제 성공")
//  void userAutoDeleteScheduling() {
//
//  }
}
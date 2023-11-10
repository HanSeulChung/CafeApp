package com.chs.cafeapp.scheduler;

import static com.chs.cafeapp.user.type.UserStatus.USER_STATUS_WITHDRAW;

import com.chs.cafeapp.coupon.repository.CouponRepository;
import com.chs.cafeapp.order.repository.OrderRepository;
import com.chs.cafeapp.user.repository.UserRepository;
import com.chs.cafeapp.user.type.UserStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class DataBaseDeleteScheduler {

  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final CouponRepository couponRepository;

  // 매일 12시에 체크
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void orderAutoDeleteScheduling() {
    LocalDateTime nowLocalDateTime = LocalDateTime.now();
    log.info("[현재 날짜와 시간] -> " + nowLocalDateTime + " 지금 시간으로부터 1년이상 초과된 주문들은 삭제됩니다.");
    orderRepository.deleteAllByUpdateDateTimeLessThan(nowLocalDateTime.minusYears(1));
  }

  // 매일 12시에 체크
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void couponAutoDeleteScheduling() {
    LocalDateTime nowLocalDateTime = LocalDateTime.now();
    log.info("[현재 날짜와 시간] -> " + nowLocalDateTime + " 지금 시간으로부터 1년이상 초과된 쿠폰들은 삭제됩니다.");
    couponRepository.deleteAllByUpdateDateTimeLessThan(nowLocalDateTime.minusYears(1));
  }

  // 매일 12시에 체크
  // 탈퇴한 회원 정보 수집 기간(2년 뒤 파기)
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void userAutoDeleteScheduling() {
    LocalDateTime nowLocalDateTime = LocalDateTime.now();
    log.info("[현재 날짜와 시간] -> " + nowLocalDateTime + " 지금 시간으로부터 탈퇴한지 2년이상 초과된 사용자들은 삭제됩니다.");
    userRepository.deleteAllByUserStatusAndUpdateDateTimeLessThan(USER_STATUS_WITHDRAW, nowLocalDateTime.minusYears(2));
  }
}

package com.chs.cafeapp.scheduler;

import com.chs.cafeapp.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DataBaseUpdateScheduler {
  private final UserRepository userRepository;

  // 매일 12시에 체크
  // 마지막 로그인한 상태가 1년 초과 일 경우 정지
  @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
  public void userAutoUpdateScheduling() {
    LocalDateTime nowLocalDateTime = LocalDateTime.now();
    userRepository.updateUserStatusForOldLogins(nowLocalDateTime.minusYears(1));
  }
}

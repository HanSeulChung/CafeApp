package com.chs.cafeapp.scheduler;

import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DataBaseUpdateScheduler {

  private final MemberRepository userRepository;

  private static final String SEOUL_TIME_ZONE = "Asia/Seoul";

  // 매일 12시에 체크
  // 마지막 로그인한 상태가 1년 초과 일 경우 정지
  @Scheduled(cron = "0 0 0 * * *", zone = SEOUL_TIME_ZONE)
  public void userAutoUpdateScheduling() {
    LocalDateTime nowLocalDateTime = LocalDateTime.now();
    // TODO: STOP 상태로 만들기 전에 사용자 이메일에 몇일 이내 다시 로그인하지 않으면 STOP된다고 고지하기
    userRepository.updateUserStatusForOldLogins(nowLocalDateTime.minusYears(1));
  }
}

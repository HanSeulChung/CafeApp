package com.chs.cafeapp.kafka.notification.service;

import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
  private final MemberService memberService;

  public void sendNotificationToUser(String userId) {
    Member user = memberService.getMemberById(userId);
    if (user != null) {
      // TODO: 알람을 보내는 로직
//      sendNotification(user);
    } else {
      // 사용자를 찾을 수 없는 경우 처리
      log.error("사용자를 찾을 수 없습니다.");
    }
  }

}

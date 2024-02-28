package com.chs.cafeapp.global.scheduler;

import static com.chs.cafeapp.auth.type.UserStatus.USER_STATUS_WITHDRAW;

import com.chs.cafeapp.domain.coupon.entity.Coupon;
import com.chs.cafeapp.domain.coupon.repository.CouponRepository;
import com.chs.cafeapp.domain.order.entity.Order;
import com.chs.cafeapp.domain.order.repository.OrderRepository;
import com.chs.cafeapp.auth.member.entity.DeleteMember;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.DeleteMemberRepository;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

  private final MemberRepository memberRepository;
  private final OrderRepository orderRepository;
  private final CouponRepository couponRepository;
  private final DeleteMemberRepository deleteUserRepository;

  private static final String SEOUL_TIME_ZONE = "Asia/Seoul";

  // 매일 12시에 체크
  @Scheduled(cron = "0 0 0 * * *", zone = SEOUL_TIME_ZONE)
  public void orderAutoDeleteScheduling() {
    LocalDateTime nowLocalDateTime = LocalDateTime.now();
    List<Order> orderList = orderRepository.findAllByUpdateDateTimeLessThan(
        nowLocalDateTime.minusYears(1));
    if (orderList.size() != 0) {
      log.info("[현재 날짜와 시간] -> {} 지금 시간으로부터 1년이상 초과된 주문들 총 {}개가 삭제되었습니다.", nowLocalDateTime, orderList.size());
      orderRepository.deleteAllByUpdateDateTimeLessThan(nowLocalDateTime.minusYears(1));
    }
  }

  // 매일 12시에 체크
  @Scheduled(cron = "0 0 0 * * *", zone = SEOUL_TIME_ZONE)
  public void couponAutoDeleteScheduling() {
    LocalDateTime nowLocalDateTime = LocalDateTime.now();
    List<Coupon> couponList = couponRepository.findAllByUpdateDateTimeLessThan(
        nowLocalDateTime.minusYears(1));
    if (couponList.size() != 0) {
      log.info("[현재 날짜와 시간] -> {} 지금 시간으로부터 1년이상 초과된 쿠폰들 총 {}개가 삭제되었습니다.", nowLocalDateTime, couponList.size());
      couponRepository.deleteAllByUpdateDateTimeLessThan(nowLocalDateTime.minusYears(1));
    }
  }

  // 매일 12시에 체크
  // 탈퇴한 회원 정보 수집 기간(2년 뒤 파기)
  @Scheduled(cron = "0 0 0 * * *", zone = SEOUL_TIME_ZONE)
  public void memberAutoDeleteScheduling() {
    LocalDateTime nowLocalDateTime = LocalDateTime.now();
    List<Member> memberList = memberRepository.findAllByMemberStatusAndUpdateDateTimeLessThan(
        USER_STATUS_WITHDRAW, nowLocalDateTime.minusYears(2));

    if (memberList.size() != 0) {
      List<DeleteMember> deleteMemberList = new ArrayList<>();
      for (Member member : memberList) {
        DeleteMember deleteMember = DeleteMember.builder()
            .loginId(member.getLoginId())
            .name(member.getName())
            .deleteDateTime(nowLocalDateTime)
            .build();
        deleteMemberList.add(deleteMember);
      }
      memberRepository.deleteAllByMemberStatusAndUpdateDateTimeLessThan(USER_STATUS_WITHDRAW, nowLocalDateTime.minusYears(2));
      deleteUserRepository.saveAll(deleteMemberList);
      log.info("[현재 날짜와 시간] -> {} 지금 시간으로부터 탈퇴한지 2년이상 초과된 사용자들 총 {}명이 삭제되었습니다.", nowLocalDateTime, deleteMemberList.size());
    }
  }
}

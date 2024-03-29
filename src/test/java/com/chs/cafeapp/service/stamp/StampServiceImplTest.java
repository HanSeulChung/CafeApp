package com.chs.cafeapp.service.stamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.chs.cafeapp.domain.coupon.service.impl.CouponServiceImpl;
import com.chs.cafeapp.domain.stamp.dto.StampDto;
import com.chs.cafeapp.domain.stamp.entity.Stamp;
import com.chs.cafeapp.domain.stamp.repository.StampRepository;
import com.chs.cafeapp.domain.stamp.service.impl.StampServiceImpl;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StampServiceImplTest {
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private StampRepository stampRepository;
  @Mock
  private CouponServiceImpl couponService;
  @InjectMocks
  private StampServiceImpl stampService;

  @Test
  @DisplayName("스탬프 추가시 최대 적립 10회를 넘었을 경우 0으로 reset 후 재적립 성공")
  void addStampNumbers_Success() {
    // given
    Member user = Member.builder()
        .id(1L)
        .loginId("user2@naver.com")
        .build();

    Stamp stamp = Stamp.builder()
        .id(1L)
        .stampNumbers(8)
        .member(user)
        .build();
    user.setStamp(stamp);

    when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.of(user));
    when(stampRepository.save(any(Stamp.class))).thenReturn(stamp);

    // when
    StampDto stampDto = stampService.addStampNumbers(4, "user2@naver.com");

    // then
    assertNotNull(stampDto);
    assertEquals(stampDto.getStampNumbers(), 8 + 4 - 10);
  }
}
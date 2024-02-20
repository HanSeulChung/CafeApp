package com.chs.cafeapp.stamp.service.impl;

import static com.chs.cafeapp.exception.type.ErrorCode.MEMBER_NOT_FOUND;

import com.chs.cafeapp.coupon.service.CouponService;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.stamp.dto.StampDto;
import com.chs.cafeapp.stamp.entity.Stamp;
import com.chs.cafeapp.stamp.repository.StampRepository;
import com.chs.cafeapp.stamp.service.StampService;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StampServiceImpl implements StampService {
  private final int MAX_STAMP_COUNT = 10;

  private final MemberRepository userRepository;
  private final StampRepository stampRepository;

  private final CouponService couponService;

  public Stamp validationUserAndStamp(String userId) {
    Member user = userRepository.findByLoginId(userId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    Stamp stamp = user.getStamp();
    stamp = Optional.ofNullable(stamp).orElseGet(() -> {
      Stamp newStamp = new Stamp();
      newStamp.setMember(user);
      user.setStamp(newStamp);
      stampRepository.save(newStamp);
      return newStamp;
    });

    return stamp;
  }
  @Override
  public StampDto viewStamp(String userId) {
    Stamp stamp = validationUserAndStamp(userId);

    return StampDto.of(stamp);
  }

  @Override
  public StampDto addStampNumbers(long stampNumbers, String userId) {

    Stamp stamp = validationUserAndStamp(userId);

    if (stamp.getStampNumbers() + stampNumbers >= MAX_STAMP_COUNT) {
      long newStampCnt = stamp.getStampNumbers() + stampNumbers - MAX_STAMP_COUNT;
      stamp.reCalculateStamp(newStampCnt);
      couponService.createCouponByStamp(userId);
      return StampDto.of(stampRepository.save(stamp));
    }

    stamp.addStamp(stampNumbers);

    return StampDto.of(stampRepository.save(stamp));
  }
}

package com.chs.cafeapp.auth.member.service.impl;

import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import com.chs.cafeapp.auth.member.service.MemberService;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.exception.type.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  private final MemberRepository memberRepository;

  @Override
  public void updateLastLoginDateTime(String loginId, LocalDateTime localDateTime) {
    Member member = memberRepository.findByLoginId(loginId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_LOGIN_ID));

    member.setLastLoginDateTime(localDateTime);
    memberRepository.save(member);
  }

  public Member getUserById(String memberId) {
    return memberRepository.findByLoginId(memberId).orElse(null);
  }
}

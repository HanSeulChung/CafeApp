package com.chs.cafeapp.auth.service;

import com.chs.cafeapp.auth.component.TokenBlackList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessTokenValidator {
  private final TokenBlackList tokenBlackList;

  public boolean isValidateToken(String accessToken) {
    // 토큰이 블랙리스트에 있는지 확인
    if (tokenBlackList.isBlacklisted(accessToken)) {
      return false; // 블랙리스트에 있는 토큰은 유효하지 않다고 판단
    }
    return true;
  }
}

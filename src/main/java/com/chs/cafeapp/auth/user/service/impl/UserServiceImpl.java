package com.chs.cafeapp.auth.user.service.impl;

import com.chs.cafeapp.auth.user.entity.User;
import com.chs.cafeapp.auth.user.repository.UserRepository;
import com.chs.cafeapp.auth.user.service.UserService;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.exception.type.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  public void updateLastLoginDateTime(String loginId, LocalDateTime localDateTime) {
    User user = userRepository.findByLoginId(loginId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_USER_LOGIN_ID));

    user.setLastLoginDateTime(localDateTime);
    userRepository.save(user);
  }

  public User getUserById(String userId) {
    return userRepository.findByLoginId(userId).orElse(null);
  }
}

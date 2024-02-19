package com.chs.cafeapp.auth.admin.service.impl;

import com.chs.cafeapp.auth.admin.entity.Admin;
import com.chs.cafeapp.auth.admin.repository.AdminRepository;
import com.chs.cafeapp.auth.admin.service.AdminService;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.exception.type.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
  private final AdminRepository adminRepository;
  @Override
  public void updateLastLoginDateTime(String loginId, LocalDateTime localDateTime) {
    Admin admin = adminRepository.findByLoginId(loginId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_LOGIN_ID));

    admin.setLastLoginDateTime(localDateTime);
    adminRepository.save(admin);
  }
}

package com.chs.cafeapp.auth.service.impl;

import static com.chs.cafeapp.auth.type.Authority.ROLE_ADMIN;
import static com.chs.cafeapp.auth.type.Authority.ROLE_YET_ADMIN;
import static com.chs.cafeapp.auth.type.UserStatus.USER_STATUS_ING;
import static com.chs.cafeapp.auth.type.UserStatus.USER_STATUS_REQ;
import static com.chs.cafeapp.global.exception.type.ErrorCode.ADMIN_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.ALREADY_EXISTS_USER_LOGIN_ID;
import static com.chs.cafeapp.global.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOTING_ACCESS_TOKEN;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_EQUALS_PASSWORD_REPASSWORD;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_ADMIN_ROLE;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_ORIGIN_PASSWORD;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_PASSWORD;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_ROLE;
import static com.chs.cafeapp.global.exception.type.ErrorCode.TOO_MANY_ROLE;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_CERTIFICATION_GUIDE;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_CERTIFICATION_SUCCESS;
import static com.chs.cafeapp.global.mail.MailConstant.TO_ADMIN;

import com.chs.cafeapp.auth.admin.dto.AdminSignUpRequestDto;
import com.chs.cafeapp.auth.admin.entity.Admin;
import com.chs.cafeapp.auth.admin.repository.AdminRepository;
import com.chs.cafeapp.auth.admin.service.AdminService;
import com.chs.cafeapp.auth.dto.AuthResponseDto;
import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.dto.SignInRequestDto;
import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.token.dto.TokenDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.global.mail.service.MailSendService;
import com.chs.cafeapp.global.mail.service.MailVerifyService;
import com.chs.cafeapp.global.redis.token.TokenRepository;
import com.chs.cafeapp.global.security.TokenProvider;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collection;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthAdminService implements AuthService {
  private final MailSendService mailSendService;
  private final MailVerifyService mailVerifyService;
  private final AdminService adminService;
  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final TokenRepository tokenRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @Transactional
  public AuthResponseDto signUp(AdminSignUpRequestDto adminSignUpRequestDto) throws MessagingException, NoSuchAlgorithmException {
    boolean existsByLoginId = adminRepository.existsByLoginId(adminSignUpRequestDto.getUsername());
    if (existsByLoginId) {
      throw new CustomException(ALREADY_EXISTS_USER_LOGIN_ID);
    }

    if (!adminSignUpRequestDto.getRePassword().equals(adminSignUpRequestDto.getPassword())) {
      throw new CustomException(NOT_EQUALS_PASSWORD_REPASSWORD);
    }

    String encPassword = passwordEncoder.encode(adminSignUpRequestDto.getPassword());

    Admin admin = Admin.builder()
        .loginId(adminSignUpRequestDto.getUsername())
        .password(encPassword)
        .name(adminSignUpRequestDto.getName())
        .adminStatus(USER_STATUS_REQ)
        .sex(adminSignUpRequestDto.getSex())
        .authority(ROLE_YET_ADMIN)
        .build();

    Admin saveAdmin = adminRepository.save(admin);

    String email = adminSignUpRequestDto.getUsername();
    mailSendService.certifiedNumberMail(email, TO_ADMIN);

    return AuthResponseDto.builder()
        .loginId(saveAdmin.getLoginId())
        .createDateTime(saveAdmin.getCreateDateTime())
        .message(MAIL_CERTIFICATION_GUIDE)
        .build();
  }
  @Override
  public TokenResponseDto signIn(SignInRequestDto signInRequestDto) {
    Admin admin = validationAdmin(signInRequestDto);

    UsernamePasswordAuthenticationToken authenticationToken = signInRequestDto.toAuthentication(signInRequestDto.getUsername(), signInRequestDto.getPassword());
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    TokenDto tokenDto = tokenProvider.saveTokenDto(authentication);

    adminService.updateLastLoginDateTime(admin.getLoginId(), LocalDateTime.now());
    return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
  }

  @Override
  public PasswordEditResponse changePassword(PasswordEditInput passwordEditInput) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Collection<? extends GrantedAuthority> authority = authentication.getAuthorities();
    if (authority.size() >= 2) {
      throw new CustomException(TOO_MANY_ROLE);
    }
    String roleName = authority.iterator().next().getAuthority();

    if (!roleName.equals("ROLE_ADMIN")) {
      throw new CustomException(NOT_MATCH_ADMIN_ROLE);
    }
    Admin admin = validationAdminByPasswordEdit(authentication, passwordEditInput.getOriginPassword());
    admin.setPassword(passwordEncoder.encode(passwordEditInput.getNewPassword()));
    adminRepository.save(admin);

    tokenRepository.saveInValidAccessToken(admin.getLoginId(), tokenRepository.getAccessToken(admin.getLoginId()));
    tokenRepository.deleteAccessToken(admin.getLoginId());
    tokenRepository.deleteRefreshToken(admin.getLoginId());
    return new PasswordEditResponse(admin.getLoginId(), "비밀번호가 변경되었습니다. 다시 로그인 후 이용해주세요.");
  }

  @Override
  public AuthResponseDto emailAuth(String email, String certifiedNumber) {
    Admin admin = adminRepository.findByLoginId(email)
        .orElseThrow(() -> new CustomException(ADMIN_NOT_FOUND));

    mailVerifyService.verifyEmail(email, certifiedNumber);
    admin.setAdminStatus(USER_STATUS_ING);
    admin.setAuthority(ROLE_ADMIN);
    adminRepository.save(admin);

    return AuthResponseDto.builder()
        .loginId(admin.getLoginId())
        .createDateTime(LocalDateTime.now())
        .message(MAIL_CERTIFICATION_SUCCESS)
        .build();
  }

  private Admin validationAdmin(SignInRequestDto signInRequestDto) {
    Admin admin = adminRepository.findByLoginId(signInRequestDto.getUsername())
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    if (!this.passwordEncoder.matches(signInRequestDto.getPassword(), admin.getPassword())) {
      throw new CustomException(NOT_MATCH_PASSWORD);
    }
    return admin;
  }

  private Admin validationAdminByPasswordEdit(Authentication authentication, String originPassword) {
    Admin admin = adminRepository.findByLoginId(authentication.getName())
        .orElseThrow(() -> new CustomException(ADMIN_NOT_FOUND));
    if (admin.getAuthority() != ROLE_ADMIN) {
      throw new CustomException(NOT_MATCH_ROLE);
    }
    if (!passwordEncoder.matches(originPassword, admin.getPassword())) {
      throw new CustomException(NOT_MATCH_ORIGIN_PASSWORD);
    }
    return admin;
  }
}

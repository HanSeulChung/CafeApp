package com.chs.cafeapp.auth.service.impl;

import static com.chs.cafeapp.auth.type.UserType.ADMIN;
import static com.chs.cafeapp.auth.type.UserType.MEMBER;
import static com.chs.cafeapp.global.exception.type.ErrorCode.INVALID_ACCESS_TOKEN;
import static com.chs.cafeapp.global.exception.type.ErrorCode.LOGOUT_MEMBER;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_EXISTS_LOGIN_ID;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_REFRESH_TOKEN_MEMBER;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_VALID_REFRESH_TOKEN;
import static com.chs.cafeapp.global.mail.MailConstant.RE_MAIL_SENDING_SUCCESS;
import static com.chs.cafeapp.global.mail.MailConstant.TO_ADMIN;
import static com.chs.cafeapp.global.mail.MailConstant.TO_MEMBER;

import com.chs.cafeapp.auth.admin.entity.Admin;
import com.chs.cafeapp.auth.admin.repository.AdminRepository;
import com.chs.cafeapp.auth.dto.LogOutResponse;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import com.chs.cafeapp.auth.service.AuthCommonService;
import com.chs.cafeapp.auth.token.dto.TokenDto;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.type.UserType;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.global.mail.service.MailSendService;
import com.chs.cafeapp.global.mail.service.MailVerifyService;
import com.chs.cafeapp.global.redis.token.TokenRepository;
import com.chs.cafeapp.global.security.TokenProvider;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommonServiceImpl implements AuthCommonService, UserDetailsService  {
  private final TokenProvider tokenProvider;
  private final TokenRepository tokenRepository;
  private final MailSendService mailSendService;
  private final MailVerifyService mailVerifyService;
  private final AdminRepository adminRepository;
  private final MemberRepository memberRepository;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails userDetailsByAdmin = adminRepository.findByLoginId(username)
        .map(this::createAdminUserDetails)
        .orElse(null);

    if (userDetailsByAdmin != null) {
      return userDetailsByAdmin;
    }

    UserDetails userDetailsByMember = memberRepository.findByLoginId(username)
        .map(this::createUserDetails)
        .orElse(null);

    if (userDetailsByMember != null) {
      return userDetailsByMember;
    }

    throw new CustomException(NOT_EXISTS_LOGIN_ID);
  }

  @Override
  public Boolean emailAuth(String email, String certifiedNumber) {
    mailVerifyService.verifyEmail(email, certifiedNumber);
    return Boolean.TRUE;
  }

  @Override
  public Boolean checkEmail(String email, UserType userType) {
    if (userType == ADMIN) {
      return !adminRepository.existsByLoginId(email);
    }

    if (userType == MEMBER) {
      return !memberRepository.existsByLoginId(email);
    }
    throw new IllegalArgumentException("UserType이 올바르지 않습니다.");
  }

  @Override
  public String reEmail(String email, UserType userType) throws NoSuchAlgorithmException, MessagingException  {
    String to = "";
    if (userType == ADMIN) {
      to = TO_ADMIN;
    }
    if (userType == MEMBER) {
      to = TO_MEMBER;
    }
    mailSendService.certifiedNumberMail(email, to);
    return RE_MAIL_SENDING_SUCCESS;
  }

  @Override
  public TokenResponseDto reIssue(TokenRequestDto tokenRequestDto) {
    String refreshToken = validateRefreshToken(tokenRequestDto);
    if (refreshToken == null) {
      new CustomException(LOGOUT_MEMBER);
    }
    Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getRefreshToken());
    validateRefreshTokenOwner(authentication.getName(), tokenRequestDto.getRefreshToken());
    TokenDto tokenDto = tokenProvider.saveTokenDto(authentication);
    tokenRepository.saveInValidAccessToken(authentication.getName(), tokenRequestDto.getAccessToken());
    return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());

  }

  @Override
  public LogOutResponse logOut(String accessToken) {
    Authentication authentication = tokenProvider.getAuthentication(accessToken);
    if(!tokenProvider.validateToken(accessToken)) {
      throw new CustomException(INVALID_ACCESS_TOKEN);
    }
    String userId = authentication.getName();
    String refreshToken = tokenRepository.getRefreshToken(userId);
    if (refreshToken == null) {
      throw new CustomException(LOGOUT_MEMBER);
    }

    tokenRepository.deleteAccessToken(userId);
    tokenRepository.deleteRefreshToken(userId);

    return new LogOutResponse(userId, "로그아웃 되었습니다.");
  }


  private String validateRefreshToken(TokenRequestDto tokenRequestDto) {
    if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
      throw new CustomException(NOT_VALID_REFRESH_TOKEN);
    }
    Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
    String userId = authentication.getName();
    return tokenRepository.getRefreshToken(userId);
  }

  private void validateRefreshTokenOwner(String userId, String refreshToken) {
    if (!tokenProvider.getUsername(refreshToken).equals(userId)) {
      throw new CustomException(NOT_MATCH_REFRESH_TOKEN_MEMBER);
    }
  }

  private UserDetails createAdminUserDetails(Admin admin) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
        admin.getAuthority().toString());

    return new User(
        admin.getLoginId(),
        admin.getPassword(),
        Collections.singleton(grantedAuthority)
    );
  }

  private UserDetails createUserDetails(Member member) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
        member.getAuthority().toString());

    return new User(
        member.getLoginId(),
        member.getPassword(),
        Collections.singleton(grantedAuthority)
    );
  }
}

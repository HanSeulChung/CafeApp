package com.chs.cafeapp.auth.service.impl;

import static com.chs.cafeapp.auth.token.type.ACCESS_TOKEN_TYPE.NO_ACCESS_TOKEN;
import static com.chs.cafeapp.auth.type.Authority.ROLE_ADMIN;
import static com.chs.cafeapp.auth.type.Authority.ROLE_MEMBER;
import static com.chs.cafeapp.auth.type.Authority.ROLE_YET_MEMBER;
import static com.chs.cafeapp.auth.member.type.MemberSex.FEMALE;
import static com.chs.cafeapp.auth.member.type.MemberSex.MALE;
import static com.chs.cafeapp.auth.member.type.MemberStatus.USER_STATUS_ING;
import static com.chs.cafeapp.auth.member.type.MemberStatus.USER_STATUS_REQ;
import static com.chs.cafeapp.exception.type.ErrorCode.ADMIN_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EMAIL_AUTH_MEMBER;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EXISTS_MEMBER_LOGIN_ID;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EXISTS_MEMBER_NICK_NAME;
import static com.chs.cafeapp.exception.type.ErrorCode.EXPIRED_DATE_TIME_FOR_EMAIL_AUTH;
import static com.chs.cafeapp.exception.type.ErrorCode.INVALID_ACCESS_TOKEN;
import static com.chs.cafeapp.exception.type.ErrorCode.LOGOUT_MEMBER;
import static com.chs.cafeapp.exception.type.ErrorCode.NOTING_ACCESS_TOKEN;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_EMAIL_AUTH;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_EXISTS_MEMBER_LOGIN_ID;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_ORIGIN_PASSWORD;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_REFRESH_TOKEN_MEMBER;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_ROLE;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_MEMBER_PASSWORD;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_VALID_REFRESH_TOKEN;
import static com.chs.cafeapp.exception.type.ErrorCode.TOO_MANY_ROLE;
import static com.chs.cafeapp.exception.type.ErrorCode.MEMBER_NOT_FOUND;

import com.chs.cafeapp.auth.admin.entity.Admin;
import com.chs.cafeapp.auth.admin.repository.AdminRepository;
import com.chs.cafeapp.auth.admin.service.AdminService;
import com.chs.cafeapp.auth.component.TokenBlackList;
import com.chs.cafeapp.auth.component.TokenPrepareList;
import com.chs.cafeapp.auth.dto.LogOutResponse;
import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.token.dto.TokenDto;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.token.entity.RefreshToken;
import com.chs.cafeapp.auth.token.repository.RefreshTokenRepository;
import com.chs.cafeapp.auth.member.dto.SignInRequestDto;
import com.chs.cafeapp.auth.member.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.member.dto.MemberResponseDto;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import com.chs.cafeapp.auth.member.service.MemberService;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.mail.service.MailService;
import com.chs.cafeapp.security.TokenProvider;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {
  private final MailService mailService;
  private final MemberService memberService;
  private final AdminService adminService;
  private final TokenProvider tokenProvider;
  private final MemberRepository memberRepository;
  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;

  private final TokenBlackList tokenBlackList;
  private final TokenPrepareList tokenPrepareList;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails userDetailsByMember = memberRepository.findByLoginId(username)
        .map(this::createUserDetails)
        .orElse(null);

    UserDetails userDetailsByAdmin = adminRepository.findByLoginId(username)
        .map(this::createAdminUserDetails)
        .orElse(null);

    if (userDetailsByMember == null && userDetailsByAdmin == null) {
      throw new CustomException(NOT_EXISTS_MEMBER_LOGIN_ID);
    }

    return userDetailsByMember == null ? userDetailsByAdmin : userDetailsByMember;
  }

  // DB 에 User 값이 존재한다면 UserDetails 객체로 생성 후 리턴
  private UserDetails createUserDetails(Member member) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
        member.getAuthority().toString());

    return new org.springframework.security.core.userdetails.User(
        member.getLoginId(),
        member.getPassword(),
        Collections.singleton(grantedAuthority)
    );
  }
  // DB 에 Admin 값이 존재한다면 UserDetails 객체로 생성 후 리턴
  private UserDetails createAdminUserDetails(Admin admin) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
        admin.getAuthority().toString());

    return new org.springframework.security.core.userdetails.User(
        admin.getLoginId(),
        admin.getPassword(),
        Collections.singleton(grantedAuthority)
    );
  }
  @Override
  @Transactional
  public MemberResponseDto signUp(SignUpRequestDto signUpRequestDto) {
    boolean existsByLoginId = memberRepository.existsByLoginId(signUpRequestDto.getUsername());
    if (existsByLoginId) {
      throw new CustomException(ALREADY_EXISTS_MEMBER_LOGIN_ID);
    }
    boolean existsByNickName = memberRepository.existsByNickName(signUpRequestDto.getNickName());
    if (existsByNickName) {
      throw new CustomException(ALREADY_EXISTS_MEMBER_NICK_NAME);
    }

    String encPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
    String uuid = UUID.randomUUID().toString();

    Member member = Member.builder()
        .loginId(signUpRequestDto.getUsername())
        .password(encPassword)
        .userName(signUpRequestDto.getName())
        .nickName(signUpRequestDto.getNickName())
        .age(signUpRequestDto.getAge())
        .sex(signUpRequestDto.getSex() == 0 ? MALE : FEMALE)
        .memberStatus(USER_STATUS_REQ)
        .emailAuthKey(uuid)
        .authority(ROLE_YET_MEMBER)
        .build();

    Member saveMember = memberRepository.save(member);

    // 회원가입 후 메일 보내기
    String email = signUpRequestDto.getUsername(); // 이메일은 로그인 아이디로 가정
    String subject = "CafeApp의 회원가입을 축하합니다";
    String text = "가입을 축하합니다. 아래 링크를 클릭하여서 가입을 완료하세요.<br>"
        + "<a href='http://localhost:8080/auth/email-auth?id=" + uuid + "'> 이메일 인증 </a>";

    mailService.sendMail(email, subject, text); // 메일 전송
    return MemberResponseDto.builder()
        .loginId(saveMember.getLoginId())
        .nickName(saveMember.getNickName())
        .createDateTime(saveMember.getCreateDateTime())
        .message("가입을 축하드립니다. 로그인 아이디로 메일을 확인해 인증을 완료한 뒤 서비스를 사용할 수 있습니다.")
        .build();
  }

  private Member validationUserBy(String uuid) {
    Member member = memberRepository.findByEmailAuthKey(uuid)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    if (member.getMemberStatus() == USER_STATUS_ING) {
      throw new CustomException(ALREADY_EMAIL_AUTH_MEMBER);
    }
    return member;
  }
  private void reSendEmail(Member member, String uuid) {
    String email = member.getLoginId();
    String subject = "CafeApp의 이메일 인증만료 후 재발송 메시지입니다.";
    String text = "가입을 축하합니다. 아래 링크를 클릭하여서 가입을 완료하세요.<br>"
        + "<a href='http://localhost:8080/auth/email-auth?id=" + uuid + "'> 이메일 인증 </a>";

    member.setUpdateDateTime();
    memberRepository.save(member);
    mailService.sendMail(email, subject, text);
  }
  @Override
  public MemberResponseDto emailAuth(String uuid) {
    Member member = validationUserBy(uuid);

    Instant requestTime = Instant.now();
    Instant expiredTime = member.getUpdateDateTime()
          .atZone(ZoneId.systemDefault()).toInstant().plus(Duration.ofHours(24));

    if (requestTime.isAfter(expiredTime)) {
      reSendEmail(member, uuid);
      throw new CustomException(EXPIRED_DATE_TIME_FOR_EMAIL_AUTH);
    }
    member.setMemberStatus(USER_STATUS_ING);
    member.setAuthority(ROLE_MEMBER);
    Member saveMember = memberRepository.save(member);

    return MemberResponseDto.builder()
        .loginId(saveMember.getLoginId())
        .nickName(saveMember.getNickName())
        .createDateTime(saveMember.getCreateDateTime())
        .message("인증이 완료되었습니다. CafeApp 서비스를 이용 가능합니다.")
        .build();
  }

  private Member validationUserFromSignIn(SignInRequestDto signInRequestDto) {
    Member member = memberRepository.findByLoginId(signInRequestDto.getUsername())
        .orElseThrow(() -> new CustomException(NOT_EXISTS_MEMBER_LOGIN_ID));
    if (!this.passwordEncoder.matches(signInRequestDto.getPassword(), member.getPassword())) {
      throw new CustomException(NOT_MATCH_MEMBER_PASSWORD);
    }
    if (member.getAuthority() == ROLE_YET_MEMBER) {
      throw new CustomException(NOT_EMAIL_AUTH);
    }
    return member;
  }

  private RefreshToken buildRefreshToken(Authentication authentication, TokenDto tokenDto) {
    return RefreshToken.builder()
        .key(authentication.getName())
        .value(tokenDto.getRefreshToken())
        .build();
  }
  @Override
  @Transactional
  public TokenResponseDto signIn(SignInRequestDto signInRequestDto) {
    Member member = validationUserFromSignIn(signInRequestDto);

    UsernamePasswordAuthenticationToken authenticationToken = signInRequestDto.toAuthentication();
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

    // 프로그램 종료 후 다시 로그인할 때 저장되어있던 것 삭제 후 refresh 저장
    boolean existsRefreshToken = refreshTokenRepository.existsByKey(signInRequestDto.getUsername());
    if (existsRefreshToken) {
      refreshTokenRepository.deleteAllByKey(signInRequestDto.getUsername());
    }
    RefreshToken refreshToken = buildRefreshToken(authentication, tokenDto);
    refreshTokenRepository.save(refreshToken);

    memberService.updateLastLoginDateTime(member.getLoginId(), LocalDateTime.now());
    return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
  }

  private Admin validationAdmin(SignInRequestDto signInRequestDto) {
    Admin admin = adminRepository.findByLoginId(signInRequestDto.getUsername())
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    if (!this.passwordEncoder.matches(signInRequestDto.getPassword(), admin.getPassword())) {
      throw new CustomException(NOT_MATCH_MEMBER_PASSWORD);
    }
    return admin;
  }
  @Override
  public TokenResponseDto adminSignIn(SignInRequestDto signInRequestDto) {
    Admin admin = validationAdmin(signInRequestDto);

    UsernamePasswordAuthenticationToken authenticationToken = signInRequestDto.toAuthentication();
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

    // 프로그램 종료 후 다시 로그인할 때 저장되어있던 것 삭제 후 refresh 저장
    boolean existsRefreshToken = refreshTokenRepository.existsByKey(signInRequestDto.getUsername());
    if (existsRefreshToken) {
      refreshTokenRepository.deleteAllByKey(signInRequestDto.getUsername());
    }
    RefreshToken refreshToken = buildRefreshToken(authentication, tokenDto);
    refreshTokenRepository.save(refreshToken);

    adminService.updateLastLoginDateTime(admin.getLoginId(), LocalDateTime.now());
    return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
  }

  private void validateRefreshToken(TokenRequestDto tokenRequestDto) {
    if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
      throw new CustomException(NOT_VALID_REFRESH_TOKEN);
    }
  }

  private void validateRefreshTokenOwner(RefreshToken refreshToken, TokenRequestDto tokenRequestDto) {
    if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
      throw new CustomException(NOT_MATCH_REFRESH_TOKEN_MEMBER);
    }
  }
  @Override
  public TokenResponseDto reIssue(TokenRequestDto tokenRequestDto) {
    validateRefreshToken(tokenRequestDto);
    Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

    RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
        .orElseThrow(() -> new CustomException(LOGOUT_MEMBER));

    validateRefreshTokenOwner(refreshToken, tokenRequestDto);

    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
    RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
    refreshTokenRepository.save(newRefreshToken);

    tokenBlackList.addToBlacklist(tokenRequestDto.getAccessToken());

    return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
  }

  @Override
  public LogOutResponse logOut(String accessToken) {
    Authentication authentication = tokenProvider.getAuthentication(accessToken);
    if(!tokenProvider.validateToken(accessToken)) {
      throw new CustomException(INVALID_ACCESS_TOKEN);
    }
    String loginId = authentication.getName();
    refreshTokenRepository.findByKey(loginId)
        .orElseThrow(() -> new CustomException(LOGOUT_MEMBER));

    refreshTokenRepository.deleteByKey(authentication.getName());
    tokenBlackList.addToBlacklist(accessToken);

    return new LogOutResponse(loginId, "로그아웃 되었습니다.");
  }

  private Member validationUserByPasswordEdit(Authentication authentication, String originPassword) {
    Member member = memberRepository.findByLoginId(authentication.getName())
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    if (member.getAuthority() != ROLE_MEMBER) {
      throw new CustomException(NOT_MATCH_ROLE);
    }
    if (!passwordEncoder.matches(originPassword, member.getPassword())) {
      throw new CustomException(NOT_MATCH_ORIGIN_PASSWORD);
    }
    return member;
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
  @Override
  public PasswordEditResponse changePassword(PasswordEditInput passwordEditInput) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Collection<? extends GrantedAuthority> authority = authentication.getAuthorities();
    if (authority.size() >= 2) {
      throw new CustomException(TOO_MANY_ROLE);
    }
    String roleName = authority.iterator().next().getAuthority();

    if (roleName.equals("ROLE_MEMBER")) {
      Member member = validationUserByPasswordEdit(authentication, passwordEditInput.getOriginPassword());
      member.changePassword(passwordEncoder.encode(passwordEditInput.getNewPassword()));
      memberRepository.save(member);
      refreshTokenRepository.deleteByKey(member.getLoginId());

      String accessToken = tokenPrepareList.getAccessToken(member.getLoginId());
      if (accessToken.equals(NO_ACCESS_TOKEN.getToken_value())) {
        throw new CustomException(NOTING_ACCESS_TOKEN);
      }

      tokenBlackList.addToBlacklist(accessToken);
      tokenPrepareList.delete(member.getLoginId());
      return new PasswordEditResponse(member.getLoginId(), "비밀번호가 변경되었습니다. 다시 로그인 후 이용해주세요.");
    }
    if (roleName.equals("ROLE_ADMIN")) {
      Admin admin = validationAdminByPasswordEdit(authentication, passwordEditInput.getOriginPassword());
      admin.changePassword(passwordEncoder.encode(passwordEditInput.getNewPassword()));
      adminRepository.save(admin);
      refreshTokenRepository.deleteByKey(admin.getLoginId());

      String accessToken = tokenPrepareList.getAccessToken(admin.getLoginId());
      if (accessToken.equals(NO_ACCESS_TOKEN.getToken_value())) {
        throw new CustomException(NOTING_ACCESS_TOKEN);
      }

      tokenBlackList.addToBlacklist(accessToken);
      tokenPrepareList.delete(admin.getLoginId());
      return new PasswordEditResponse(admin.getLoginId(), "비밀번호가 변경되었습니다. 다시 로그인 후 이용해주세요.");
    }
    return new PasswordEditResponse(authentication.getName(), "비밀번호가 변경되지 않았습니다.");
  }
}

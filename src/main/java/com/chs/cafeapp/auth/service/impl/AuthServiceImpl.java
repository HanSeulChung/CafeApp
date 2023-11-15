package com.chs.cafeapp.auth.service.impl;

import static com.chs.cafeapp.auth.type.Authority.ROLE_USER;
import static com.chs.cafeapp.auth.type.Authority.ROLE_YET_USER;
import static com.chs.cafeapp.auth.user.type.UserSex.FEMALE;
import static com.chs.cafeapp.auth.user.type.UserSex.MALE;
import static com.chs.cafeapp.auth.user.type.UserStatus.USER_STATUS_ING;
import static com.chs.cafeapp.auth.user.type.UserStatus.USER_STATUS_REQ;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EMAIL_AUTH_USER;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EXISTS_USER_LOGIN_ID;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EXISTS_USER_NICK_NAME;
import static com.chs.cafeapp.exception.type.ErrorCode.EXPIRED_DATE_TIME_FOR_EMAIL_AUTH;
import static com.chs.cafeapp.exception.type.ErrorCode.LOGOUT_USER;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_EMAIL_AUTH;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_EXISTS_USER_LOGIN_ID;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_REFRESH_TOKEN_USER;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_USER_PASSWORD;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_VALID_REFRESH_TOKEN;
import static com.chs.cafeapp.exception.type.ErrorCode.USER_NOT_FOUND;

import com.chs.cafeapp.auth.admin.entity.Admin;
import com.chs.cafeapp.auth.admin.repository.AdminRepository;
import com.chs.cafeapp.auth.admin.service.AdminService;
import com.chs.cafeapp.auth.dto.LogOutResponse;
import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.token.dto.TokenDto;
import com.chs.cafeapp.auth.token.dto.TokenRequestDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.token.entity.RefreshToken;
import com.chs.cafeapp.auth.token.repository.RefreshTokenRepository;
import com.chs.cafeapp.auth.user.dto.SignInRequestDto;
import com.chs.cafeapp.auth.user.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.user.dto.UserResponseDto;
import com.chs.cafeapp.auth.user.entity.User;
import com.chs.cafeapp.auth.user.repository.UserRepository;
import com.chs.cafeapp.auth.user.service.UserService;
import com.chs.cafeapp.config.security.SecurityConfig;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.mail.service.MailService;
import com.chs.cafeapp.security.TokenProvider;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
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
  private final UserService userService;
  private final AdminService adminService;
  private final TokenProvider tokenProvider;
  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;

  private final RefreshTokenRepository refreshTokenRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails userDetailsByUser = userRepository.findByLoginId(username)
        .map(this::createUserDetails)
        .orElse(null);

    UserDetails userDetailsByAdmin = adminRepository.findByLoginId(username)
        .map(this::createAdminUserDetails)
        .orElse(null);

    if (userDetailsByUser == null && userDetailsByAdmin == null) {
      throw new CustomException(NOT_EXISTS_USER_LOGIN_ID);
    }

    return userDetailsByUser == null ? userDetailsByAdmin : userDetailsByUser;
  }

  // DB 에 User 값이 존재한다면 UserDetails 객체로 생성 후 리턴
  private UserDetails createUserDetails(User user) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
        user.getAuthority().toString());

    return new org.springframework.security.core.userdetails.User(
        user.getLoginId(),
        user.getPassword(),
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
  public UserResponseDto signUp(SignUpRequestDto signUpRequestDto) {
    boolean existsByLoginId = userRepository.existsByLoginId(signUpRequestDto.getUsername());
    if (existsByLoginId) {
      throw new CustomException(ALREADY_EXISTS_USER_LOGIN_ID);
    }
    boolean existsByNickName = userRepository.existsByNickName(signUpRequestDto.getNickName());
    if (existsByNickName) {
      throw new CustomException(ALREADY_EXISTS_USER_NICK_NAME);
    }

    String encPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
    String uuid = UUID.randomUUID().toString();

    User user = User.builder()
        .loginId(signUpRequestDto.getUsername())
        .password(encPassword)
        .userName(signUpRequestDto.getName())
        .nickName(signUpRequestDto.getNickName())
        .age(signUpRequestDto.getAge())
        .sex(signUpRequestDto.getSex() == 0 ? MALE : FEMALE)
        .userStatus(USER_STATUS_REQ)
        .emailAuthKey(uuid)
        .authority(ROLE_YET_USER)
        .build();

    User saveUser = userRepository.save(user);

    // 회원가입 후 메일 보내기
    String email = signUpRequestDto.getUsername(); // 이메일은 로그인 아이디로 가정
    String subject = "CafeApp의 회원가입을 축하합니다";
    String text = "가입을 축하합니다. 아래 링크를 클릭하여서 가입을 완료하세요.<br>"
        + "<a href='http://localhost:8080/auth/email-auth?id=" + uuid + "'> 이메일 인증 </a>";

    mailService.sendMail(email, subject, text); // 메일 전송
    return UserResponseDto.builder()
        .loginId(saveUser.getLoginId())
        .nickName(saveUser.getNickName())
        .createDateTime(saveUser.getCreateDateTime())
        .message("가입을 축하드립니다. 로그인 아이디로 메일을 확인해 인증을 완료한 뒤 서비스를 사용할 수 있습니다.")
        .build();
  }

  private User validationUser(String uuid) {
    User user = userRepository.findByEmailAuthKey(uuid)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (user.getUserStatus() == USER_STATUS_ING) {
      throw new CustomException(ALREADY_EMAIL_AUTH_USER);
    }
    return user;
  }
  private void reSendEmail(User user, String uuid) {
    String email = user.getLoginId();
    String subject = "CafeApp의 이메일 인증만료 후 재발송 메시지입니다.";
    String text = "가입을 축하합니다. 아래 링크를 클릭하여서 가입을 완료하세요.<br>"
        + "<a href='http://localhost:8080/auth/email-auth?id=" + uuid + "'> 이메일 인증 </a>";

    user.setUpdateDateTime();
    userRepository.save(user);
    mailService.sendMail(email, subject, text);
  }
  @Override
  public UserResponseDto emailAuth(String uuid) {
    User user = validationUser(uuid);

    Instant requestTime = Instant.now();
    Instant expiredTime = user.getUpdateDateTime()
          .atZone(ZoneId.systemDefault()).toInstant().plus(Duration.ofHours(24));

    if (requestTime.isAfter(expiredTime)) {
      reSendEmail(user, uuid);
      throw new CustomException(EXPIRED_DATE_TIME_FOR_EMAIL_AUTH);
    }
    user.setUserStatus(USER_STATUS_ING);
    user.setAuthority(ROLE_USER);
    User saveUser = userRepository.save(user);

    return UserResponseDto.builder()
        .loginId(saveUser.getLoginId())
        .nickName(saveUser.getNickName())
        .createDateTime(saveUser.getCreateDateTime())
        .message("인증이 완료되었습니다. CafeApp 서비스를 이용 가능합니다.")
        .build();
  }

  private User validationUser(SignInRequestDto signInRequestDto) {
    User user = userRepository.findByLoginId(signInRequestDto.getUsername())
        .orElseThrow(() -> new CustomException(NOT_EXISTS_USER_LOGIN_ID));
    if (!this.passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
      throw new CustomException(NOT_MATCH_USER_PASSWORD);
    }
    if (user.getAuthority() == ROLE_YET_USER) {
      throw new CustomException(NOT_EMAIL_AUTH);
    }
    return user;
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
    User user = validationUser(signInRequestDto);

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

    userService.updateLastLoginDateTime(user.getLoginId(), LocalDateTime.now());
    return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
  }

  private Admin validationAdmin(SignInRequestDto signInRequestDto) {
    Admin admin = adminRepository.findByLoginId(signInRequestDto.getUsername())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (!this.passwordEncoder.matches(signInRequestDto.getPassword(), admin.getPassword())) {
      throw new CustomException(NOT_MATCH_USER_PASSWORD);
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
      throw new CustomException(NOT_MATCH_REFRESH_TOKEN_USER);
    }
  }
  @Override
  public TokenResponseDto reIssue(TokenRequestDto tokenRequestDto) {
    validateRefreshToken(tokenRequestDto);
    Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

    RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
        .orElseThrow(() -> new CustomException(LOGOUT_USER));

    validateRefreshTokenOwner(refreshToken, tokenRequestDto);

    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
    RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
    refreshTokenRepository.save(newRefreshToken);

    return new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
  }

  @Override
  public LogOutResponse logOut(String accessToken) {
    Authentication authentication = tokenProvider.getAuthentication(accessToken);

    String loginId = authentication.getName();
    refreshTokenRepository.findByKey(loginId)
        .orElseThrow(() -> new CustomException(LOGOUT_USER));

    refreshTokenRepository.deleteByKey(authentication.getName());

    return new LogOutResponse(loginId, "로그아웃 되었습니다.");
  }
}

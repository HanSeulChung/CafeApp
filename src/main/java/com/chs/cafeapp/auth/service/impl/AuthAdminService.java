package com.chs.cafeapp.auth.service.impl;

import static com.chs.cafeapp.auth.token.type.ACCESS_TOKEN_TYPE.NO_ACCESS_TOKEN;
import static com.chs.cafeapp.auth.type.Authority.ROLE_ADMIN;
import static com.chs.cafeapp.exception.type.ErrorCode.ADMIN_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static com.chs.cafeapp.exception.type.ErrorCode.NOTING_ACCESS_TOKEN;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_ADMIN_ROLE;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_ORIGIN_PASSWORD;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_PASSWORD;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_ROLE;
import static com.chs.cafeapp.exception.type.ErrorCode.TOO_MANY_ROLE;

import com.chs.cafeapp.auth.admin.entity.Admin;
import com.chs.cafeapp.auth.admin.repository.AdminRepository;
import com.chs.cafeapp.auth.admin.service.AdminService;
import com.chs.cafeapp.auth.component.TokenBlackList;
import com.chs.cafeapp.auth.component.TokenPrepareList;
import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.member.dto.AuthResponseDto;
import com.chs.cafeapp.auth.member.dto.SignInRequestDto;
import com.chs.cafeapp.auth.member.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.token.dto.TokenDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.token.entity.RefreshToken;
import com.chs.cafeapp.auth.token.repository.RefreshTokenRepository;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.security.TokenProvider;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthAdminService implements AuthService {

  private final AdminService adminService;
  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final TokenBlackList tokenBlackList;
  private final TokenPrepareList tokenPrepareList;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  // TODO: 카페 관리자 회원가입
  @Override
  @Transactional
  public AuthResponseDto signUp(SignUpRequestDto signUpRequestDto) {
//
//    if (!signUpRequestDto.getRePassword().equals(signUpRequestDto.getPassword())) {
//      throw new CustomException(NOT_EQUALS_PASSWORD_REPASSWORD);
//    }
//
//    String encPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
//    String uuid = UUID.randomUUID().toString();
//
//    Member member = Member.builder()
//        .loginId(signUpRequestDto.getUsername())
//        .password(encPassword)
//        .userName(signUpRequestDto.getName())
//        .nickName(signUpRequestDto.getNickName())
//        .age(signUpRequestDto.getAge())
//        .sex(signUpRequestDto.getSex() == 0 ? MALE : FEMALE)
//        .memberStatus(USER_STATUS_REQ)
//        .emailAuthKey(uuid)
//        .authority(ROLE_YET_MEMBER)
//        .build();
//
//    Member saveMember = memberRepository.save(member);
//
//    // 회원가입 후 메일 보내기
//    String email = signUpRequestDto.getUsername(); // 이메일은 로그인 아이디로 가정
//    String subject = "CafeApp의 회원가입을 축하합니다";
//    String text = "가입을 축하합니다. 아래 링크를 클릭하여서 가입을 완료하세요.<br>"
//        + "<a href='http://localhost:8080/auth/email-auth?id=" + uuid + "'> 이메일 인증 </a>";
//
//    mailService.sendMail(email, subject, text); // 메일 전송
//    return MemberResponseDto.builder()
//        .loginId(saveMember.getLoginId())
//        .nickName(saveMember.getNickName())
//        .createDateTime(saveMember.getCreateDateTime())
//        .message("가입을 축하드립니다. 로그인 아이디로 메일을 확인해 인증을 완료한 뒤 서비스를 사용할 수 있습니다.")
//        .build();
//  }

//  private Member validationUserBy(String uuid) {
//    Member member = memberRepository.findByEmailAuthKey(uuid)
//        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
//
//    if (member.getMemberStatus() == USER_STATUS_ING) {
//      throw new CustomException(ALREADY_EMAIL_AUTH_MEMBER);
//    }
    return null;
  }
  @Override
  public TokenResponseDto signIn(SignInRequestDto signInRequestDto) {
    Admin admin = validationAdmin(signInRequestDto);

    UsernamePasswordAuthenticationToken authenticationToken = signInRequestDto.toAuthentication(signInRequestDto.getUsername(), signInRequestDto.getPassword());
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

  @Override
  public AuthResponseDto emailAuth(String uuid) {
    return null;
  }

  private RefreshToken buildRefreshToken(Authentication authentication, TokenDto tokenDto) {
    return RefreshToken.builder()
        .key(authentication.getName())
        .value(tokenDto.getRefreshToken())
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

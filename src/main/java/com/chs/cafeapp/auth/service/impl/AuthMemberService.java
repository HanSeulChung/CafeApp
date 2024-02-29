package com.chs.cafeapp.auth.service.impl;

import static com.chs.cafeapp.auth.type.Authority.ROLE_MEMBER;
import static com.chs.cafeapp.auth.type.Authority.ROLE_YET_MEMBER;
import static com.chs.cafeapp.auth.type.UserStatus.USER_STATUS_ING;
import static com.chs.cafeapp.auth.type.UserStatus.USER_STATUS_REQ;
import static com.chs.cafeapp.global.exception.type.ErrorCode.ALREADY_EXISTS_MEMBER_NICK_NAME;
import static com.chs.cafeapp.global.exception.type.ErrorCode.ALREADY_EXISTS_USER_LOGIN_ID;
import static com.chs.cafeapp.global.exception.type.ErrorCode.MEMBER_NOT_FOUND;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOTING_ACCESS_TOKEN;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_EMAIL_AUTH;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_EQUALS_PASSWORD_REPASSWORD;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_EXISTS_LOGIN_ID;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_MEMBER_ROLE;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_ORIGIN_PASSWORD;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_PASSWORD;
import static com.chs.cafeapp.global.exception.type.ErrorCode.NOT_MATCH_ROLE;
import static com.chs.cafeapp.global.exception.type.ErrorCode.TOO_MANY_ROLE;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_CERTIFICATION_GUIDE;
import static com.chs.cafeapp.global.mail.MailConstant.MAIL_CERTIFICATION_SUCCESS;
import static com.chs.cafeapp.global.mail.MailConstant.TO_MEMBER;

import com.chs.cafeapp.auth.component.TokenBlackList;
import com.chs.cafeapp.auth.component.TokenPrepareList;
import com.chs.cafeapp.auth.dto.AuthResponseDto;
import com.chs.cafeapp.auth.dto.PasswordEditInput;
import com.chs.cafeapp.auth.dto.PasswordEditResponse;
import com.chs.cafeapp.auth.dto.SignInRequestDto;
import com.chs.cafeapp.auth.member.dto.MemberSignUpRequestDto;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import com.chs.cafeapp.auth.member.service.MemberService;
import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.token.dto.TokenDto;
import com.chs.cafeapp.auth.token.dto.TokenResponseDto;
import com.chs.cafeapp.auth.token.repository.RefreshTokenRepository;
import com.chs.cafeapp.global.exception.CustomException;
import com.chs.cafeapp.global.mail.service.MailSendService;
import com.chs.cafeapp.global.mail.service.MailVerifyService;
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
public class AuthMemberService implements AuthService{
  private final MailSendService mailSendService;
  private final MailVerifyService mailVerifyService;
  private final MemberService memberService;
  private final TokenProvider tokenProvider;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  private final TokenBlackList tokenBlackList;
  private final TokenPrepareList tokenPrepareList;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;


  @Transactional
  public AuthResponseDto signUp(MemberSignUpRequestDto memberSignUpRequestDto) throws MessagingException, NoSuchAlgorithmException {
    boolean existsByLoginId = memberRepository.existsByLoginId(memberSignUpRequestDto.getUsername());
    if (existsByLoginId) {
      throw new CustomException(ALREADY_EXISTS_USER_LOGIN_ID);
    }
    boolean existsByNickName = memberRepository.existsByNickName(memberSignUpRequestDto.getNickName());
    if (existsByNickName) {
      throw new CustomException(ALREADY_EXISTS_MEMBER_NICK_NAME);
    }

    if (!memberSignUpRequestDto.getRePassword().equals(memberSignUpRequestDto.getPassword())) {
      throw new CustomException(NOT_EQUALS_PASSWORD_REPASSWORD);
    }

    String encPassword = passwordEncoder.encode(memberSignUpRequestDto.getPassword());

    Member member = Member.builder()
        .loginId(memberSignUpRequestDto.getUsername())
        .password(encPassword)
        .name(memberSignUpRequestDto.getName())
        .nickName(memberSignUpRequestDto.getNickName())
        .age(memberSignUpRequestDto.getAge())
        .sex(memberSignUpRequestDto.getSex())
        .memberStatus(USER_STATUS_REQ)
        .authority(ROLE_YET_MEMBER)
        .build();

    Member saveMember = memberRepository.save(member);


    String email = memberSignUpRequestDto.getUsername(); // 이메일은 로그인 아이디로 가정
    mailSendService.certifiedNumberMail(email, TO_MEMBER); // 메일 전송
    return AuthResponseDto.builder()
        .loginId(saveMember.getLoginId())
        .createDateTime(saveMember.getCreateDateTime())
        .message(MAIL_CERTIFICATION_GUIDE)
        .build();
  }

  @Override
  public AuthResponseDto emailAuth(String email, String certifiedNumber) {
    Member member = memberRepository.findByLoginId(email)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    mailVerifyService.verifyEmail(email, certifiedNumber);
    member.setMemberStatus(USER_STATUS_ING);
    member.setAuthority(ROLE_MEMBER);
    memberRepository.save(member);

    return AuthResponseDto.builder()
        .loginId(member.getLoginId())
        .createDateTime(LocalDateTime.now())
        .message(MAIL_CERTIFICATION_SUCCESS)
        .build();
  }

  @Override
  @Transactional
  public TokenResponseDto signIn(SignInRequestDto signInRequestDto) {
    Member member = validationUserFromSignIn(signInRequestDto);

    UsernamePasswordAuthenticationToken authenticationToken = signInRequestDto.toAuthentication(signInRequestDto.getUsername(), signInRequestDto.getPassword());
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

    // 프로그램 종료 후 다시 로그인할 때 저장되어있던 것 삭제 후 refresh 저장
    boolean existsRefreshToken = refreshTokenRepository.existsByKey(signInRequestDto.getUsername());
    if (existsRefreshToken) {
      refreshTokenRepository.deleteAllByKey(signInRequestDto.getUsername());
    }
//    RefreshToken refreshToken = buildRefreshToken(authentication, tokenDto);
//    refreshTokenRepository.save(refreshToken);

    memberService.updateLastLoginDateTime(member.getLoginId(), LocalDateTime.now());
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

    if (!roleName.equals("ROLE_MEMBER")) {
      throw new CustomException(NOT_MATCH_MEMBER_ROLE);
    }
    Member member = validationMemberByPasswordEdit(authentication, passwordEditInput.getOriginPassword());
    member.setPassword(passwordEncoder.encode(passwordEditInput.getNewPassword()));
    memberRepository.save(member);
    refreshTokenRepository.deleteByKey(member.getLoginId());

    String accessToken = tokenPrepareList.getAccessToken(member.getLoginId());
    if (accessToken == null) {
      throw new CustomException(NOTING_ACCESS_TOKEN);
    }

    tokenBlackList.addToBlacklist(accessToken);
    tokenPrepareList.delete(member.getLoginId());
    return new PasswordEditResponse(member.getLoginId(), "비밀번호가 변경되었습니다. 다시 로그인 후 이용해주세요.");
  }

  private void reSendEmail(Member member, String uuid) throws MessagingException{
    String email = member.getLoginId();
    String subject = "CafeApp의 이메일 인증만료 후 재발송 메시지입니다.";
    String text = "가입을 축하합니다. 아래 링크를 클릭하여서 가입을 완료하세요.<br>"
        + "<a href='http://localhost:8080/auth/email-auth?id=" + uuid + "'> 이메일 인증 </a>";

    mailSendService.sendMail(email, subject, text);
  }

  private Member validationUserFromSignIn(SignInRequestDto signInRequestDto) {
    Member member = memberRepository.findByLoginId(signInRequestDto.getUsername())
        .orElseThrow(() -> new CustomException(NOT_EXISTS_LOGIN_ID));
    if (!this.passwordEncoder.matches(signInRequestDto.getPassword(), member.getPassword())) {
      throw new CustomException(NOT_MATCH_PASSWORD);
    }
    if (member.getAuthority() == ROLE_YET_MEMBER) {
      throw new CustomException(NOT_EMAIL_AUTH);
    }
    return member;
  }

//  private RefreshToken buildRefreshToken(Authentication authentication, TokenDto tokenDto) {
//    return RefreshToken.builder()
//        .key(authentication.getName())
//        .value(tokenDto.getRefreshToken())
//        .build();
//  }

  private Member validationMemberByPasswordEdit(Authentication authentication, String originPassword) {
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
}

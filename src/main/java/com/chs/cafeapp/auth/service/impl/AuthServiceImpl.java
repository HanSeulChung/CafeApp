package com.chs.cafeapp.auth.service.impl;

import static com.chs.cafeapp.auth.type.Authority.ROLE_USER;
import static com.chs.cafeapp.auth.type.Authority.ROLE_YET_USER;
import static com.chs.cafeapp.auth.user.type.UserSex.FEMALE;
import static com.chs.cafeapp.auth.user.type.UserSex.MALE;
import static com.chs.cafeapp.auth.user.type.UserStatus.USER_STATUS_ING;
import static com.chs.cafeapp.auth.user.type.UserStatus.USER_STATUS_REQ;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EXISTS_USER_LOGIN_ID;
import static com.chs.cafeapp.exception.type.ErrorCode.ALREADY_EXISTS_USER_NICK_NAME;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_EXISTS_USER_LOGIN_ID;
import static com.chs.cafeapp.exception.type.ErrorCode.USER_NOT_FOUND;

import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.user.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.user.dto.UserResponseDto;
import com.chs.cafeapp.auth.user.entity.User;
import com.chs.cafeapp.auth.user.repository.UserRepository;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.exception.type.ErrorCode;
import com.chs.cafeapp.mail.service.MailService;
import java.util.Collections;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByLoginId(username)
        .map(this::createUserDetails)
        .orElseThrow(() -> new CustomException(NOT_EXISTS_USER_LOGIN_ID));
  }

  // DB 에 User 값이 존재한다면 UserDetails 객체로 생성 후 리턴
  private UserDetails createUserDetails(User user) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

    // TODO: 로그인시 database에 user값이 존재하면 UserDetails 객체 생성 후 리턴
    return null;
  }

  @Override
  @Transactional
  public UserResponseDto signup(SignUpRequestDto signUpRequestDto) {
    boolean existsByLoginId = userRepository.existsByLoginId(signUpRequestDto.getLoginId());
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
        .loginId(signUpRequestDto.getLoginId())
        .password(encPassword)
        .userName(signUpRequestDto.getUserName())
        .nickName(signUpRequestDto.getNickName())
        .age(signUpRequestDto.getAge())
        .sex(signUpRequestDto.getSex() == 0 ? MALE : FEMALE)
        .userStatus(USER_STATUS_REQ)
        .emailAuthKey(uuid)
        .authority(ROLE_YET_USER)
        .build();

    User saveUser = userRepository.save(user);

    // 회원가입 후 메일 보내기
    String email = signUpRequestDto.getLoginId(); // 이메일은 로그인 아이디로 가정
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

  @Override
  public UserResponseDto emailAuth(String uuid) {
    User user = userRepository.findByEmailAuthKey(uuid)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

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
}

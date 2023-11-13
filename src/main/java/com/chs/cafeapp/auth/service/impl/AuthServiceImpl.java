package com.chs.cafeapp.auth.service.impl;

import static com.chs.cafeapp.auth.type.Authority.ROLE_USER;
import static com.chs.cafeapp.auth.user.type.UserSex.FEMALE;
import static com.chs.cafeapp.auth.user.type.UserSex.MALE;
import static com.chs.cafeapp.auth.user.type.UserStatus.USER_STATUS_REQ;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_EXISTS_USER_LOGIN_ID;

import com.chs.cafeapp.auth.service.AuthService;
import com.chs.cafeapp.auth.user.dto.SignUpRequestDto;
import com.chs.cafeapp.auth.user.dto.UserResponseDto;
import com.chs.cafeapp.auth.user.entity.User;
import com.chs.cafeapp.auth.user.repository.UserRepository;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.exception.type.ErrorCode;
import java.util.Collections;
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
    boolean exists = userRepository.existsByLoginId(signUpRequestDto.getLoginId());
    if (exists) {
      throw new CustomException(ErrorCode.ALREADY_EXISTS_USER_LOGIN_ID);
    }

    User user = User.builder()
        .loginId(signUpRequestDto.getLoginId())
        .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
        .userName(signUpRequestDto.getUserName())
        .nickName(signUpRequestDto.getNickName())
        .age(signUpRequestDto.getAge())
        .sex(signUpRequestDto.getSex() == 0 ? MALE : FEMALE)
        .userStatus(USER_STATUS_REQ)
        .authority(ROLE_USER)
        .build();

    User saveUser = userRepository.save(user);

    return UserResponseDto.builder()
        .loginId(saveUser.getLoginId())
        .nickName(saveUser.getNickName())
        .createDateTime(saveUser.getCreateDateTime())
        .message("가입을 축하드립니다. 로그인 아이디로 메일을 확인해 인증을 완료한 뒤 서비스를 사용할 수 있습니다.")
        .build();
  }
}

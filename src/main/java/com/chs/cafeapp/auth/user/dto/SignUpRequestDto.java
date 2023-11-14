package com.chs.cafeapp.auth.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpRequestDto {

  private String username; //loginId
  private String password;
  private String name;
  private String nickName;
  private int age;
  private int sex; // 0이면 MALE, 1이면 FEMALE

}

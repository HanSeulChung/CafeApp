package com.chs.cafeapp.auth.member.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpRequestDto {
  @NotBlank
  private String username; //loginId
  @NotBlank
  private String password;
  @NotBlank
  private String rePassword;
  @NotBlank
  private String name;
  @NotBlank
  private String nickName;
  @NotNull
  private int age;
  @NotNull
  private int sex; // 0이면 MALE, 1이면 FEMALE

}

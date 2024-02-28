package com.chs.cafeapp.auth.admin.dto;

import com.chs.cafeapp.auth.type.UserSex;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminSignUpRequestDto {
  @NotBlank
  private String username; //loginId
  @NotBlank
  private String password;
  @NotBlank
  private String rePassword;
  @NotBlank
  private String name;
  @NotNull
  private int age;
  @NotNull
  private UserSex sex;
}

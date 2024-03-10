package com.chs.cafeapp.auth.member.dto;

import com.chs.cafeapp.auth.type.UserSex;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpRequestDto {
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
  private UserSex sex;
}

package com.chs.cafeapp.global.mail.dto;

import com.chs.cafeapp.auth.type.UserType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EmailRequest {

  @Email
  @NotBlank
  private String email;

  @NotNull
  private UserType userType;
}

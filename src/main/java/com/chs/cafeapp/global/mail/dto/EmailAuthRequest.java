package com.chs.cafeapp.global.mail.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailAuthRequest {

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String authNumber;
}

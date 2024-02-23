package com.chs.cafeapp.auth.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Builder
@AllArgsConstructor
public class SignInRequestDto {
  @NotBlank
  private String username; //loginId
  @NotBlank
  private String password;


  public UsernamePasswordAuthenticationToken toAuthentication(String username, String password) {
    return new UsernamePasswordAuthenticationToken(username, password);
  }
}

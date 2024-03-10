package com.chs.cafeapp.auth.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {
  @NotBlank
  private String username; //loginId
  @NotBlank
  private String password;


  public UsernamePasswordAuthenticationToken toAuthentication(String username, String password) {
    return new UsernamePasswordAuthenticationToken(username, password);
  }
}

package com.chs.cafeapp.auth.token.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ACCESS_TOKEN_TYPE {
  NO_ACCESS_TOKEN(" ");
  private String token_value;
}

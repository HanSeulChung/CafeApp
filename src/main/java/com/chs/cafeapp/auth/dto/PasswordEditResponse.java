package com.chs.cafeapp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordEditResponse {
  String loginId;
  String message;
}

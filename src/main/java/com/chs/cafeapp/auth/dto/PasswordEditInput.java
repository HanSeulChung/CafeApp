package com.chs.cafeapp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordEditInput {
  String loginId;
  String originPassword;
  String newPassword;
}

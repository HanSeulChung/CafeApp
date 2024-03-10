package com.chs.cafeapp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordEditInput {
  String loginId;
  String originPassword;
  String newPassword;
}

package com.chs.cafeapp.global.exception.dto;

import com.chs.cafeapp.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {
  private ErrorCode errorCode;
  private int httpCode;
  private String ErrorMessage;
}



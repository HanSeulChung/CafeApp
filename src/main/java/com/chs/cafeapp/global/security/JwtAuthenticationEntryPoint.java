package com.chs.cafeapp.global.security;

import static com.chs.cafeapp.global.exception.type.ErrorCode.UN_AUTHORIZATION;

import com.chs.cafeapp.global.exception.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    ErrorResponse errorResponse;

    // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    errorResponse = ErrorResponse.builder()
        .httpCode(401)
        .errorCode(UN_AUTHORIZATION)
        .ErrorMessage(UN_AUTHORIZATION.getDescription())
        .build();

    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    response.flushBuffer();

  }
}

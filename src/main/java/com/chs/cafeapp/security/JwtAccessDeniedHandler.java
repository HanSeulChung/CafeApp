package com.chs.cafeapp.security;

import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_AUTHORIZATION;

import com.chs.cafeapp.exception.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    ErrorResponse errorResponse;

    // 필요한 권한이 없이 접근하려 할때 403
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    errorResponse = ErrorResponse.builder()
        .httpCode(403)
        .errorCode(NOT_MATCH_AUTHORIZATION)
        .ErrorMessage(NOT_MATCH_AUTHORIZATION.getDescription())
        .build();

    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    response.flushBuffer();
  }
}

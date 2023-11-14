package com.chs.cafeapp.exception;


import static com.chs.cafeapp.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.chs.cafeapp.exception.type.ErrorCode.INVALID_REQUEST;
import static com.chs.cafeapp.exception.type.ErrorCode.NOT_MATCH_AUTHORIZATION;
import static com.chs.cafeapp.exception.type.ErrorCode.UN_AUTHORIZATION;

import com.chs.cafeapp.exception.dto.ErrorResponse;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthorizationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustomException(CustomException e) {
        log.error("{} is occurred.", e.getErrorMessage());
        return new ErrorResponse(e.getErrorCode(), e.getErrorCode().getHttpCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException is occurred.", e);
        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getHttpCode(), INVALID_REQUEST.getDescription());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException is occurred.", e);
        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getHttpCode(), INVALID_REQUEST.getDescription());
    }
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handleAuthenticationException(Exception e) {
        log.error("AuthenticationException is occurred.", e);
        return new ErrorResponse(UN_AUTHORIZATION, UN_AUTHORIZATION.getHttpCode(), UN_AUTHORIZATION.getDescription());
    }
    @ExceptionHandler(ClaimJwtException.class)
    public ErrorResponse handleExpiredJwtException(Exception e) {
        log.error("ExpiredJwtException is occurred.", e);
        return new ErrorResponse(UN_AUTHORIZATION, UN_AUTHORIZATION.getHttpCode(), UN_AUTHORIZATION.getDescription());
    }

    /**
     * ROLE_USER 토큰으로 ROLE_ADMIN 접근권한 페이지를 사용하려고할때(그 반대상황도 마찬가지)
     * @param e
     * @return CustomException
     * {
     *     "errorCode": "NOT_MATCH_AUTHORIZATION",
     *     "httpCode": 403,
     *     "errorMessage": "접근 권한이 올바르지 않습니다."
     * }
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(Exception e) {
        log.error("IllegalArgumentException is occurred.", e);
        return new ErrorResponse(NOT_MATCH_AUTHORIZATION, NOT_MATCH_AUTHORIZATION.getHttpCode(), NOT_MATCH_AUTHORIZATION.getDescription());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(Exception e) {
        log.error("AccessDeniedException is occurred.", e);
        return new ErrorResponse(NOT_MATCH_AUTHORIZATION, NOT_MATCH_AUTHORIZATION.getHttpCode(), NOT_MATCH_AUTHORIZATION.getDescription());
    }
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception is occurred.", e);
        return new ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getHttpCode(), INTERNAL_SERVER_ERROR.getDescription());
    }
}

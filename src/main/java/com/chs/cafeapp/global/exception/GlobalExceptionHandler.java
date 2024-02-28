package com.chs.cafeapp.global.exception;


import static com.chs.cafeapp.global.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.chs.cafeapp.global.exception.type.ErrorCode.INVALID_REQUEST;

import com.chs.cafeapp.global.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("{} is occurred.", e.getErrorMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getErrorCode(), e.getErrorCode().getHttpCode(), e.getErrorMessage()),
            HttpStatus.resolve(e.getErrorCode().getHttpCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException is occurred.", e);
        return new ResponseEntity<>(new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getHttpCode(), INVALID_REQUEST.getDescription()),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException is occurred.", e);
        return new ResponseEntity<>(new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getHttpCode(), INVALID_REQUEST.getDescription()),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointException(NullPointerException e) {
        log.error("NullPointerException is occurred.", e);
        return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getHttpCode(), INTERNAL_SERVER_ERROR.getDescription()),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception is occurred.", e);
        return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getHttpCode(), INTERNAL_SERVER_ERROR.getDescription()),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

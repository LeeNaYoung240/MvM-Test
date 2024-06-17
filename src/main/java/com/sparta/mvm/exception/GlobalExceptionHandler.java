package com.sparta.mvm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity handleCustomException(CustomException ex) {
        HttpStatus status = ex.getStatusEnum() == ErrorEnum.BAD_POSTID ? HttpStatus.NOT_FOUND : HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(CommonResponse.builder()
                .statusCode(ex.getStatusEnum().getStatusCode())
                .msg(ex.getStatusEnum().getMsg())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CommonResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.ok().body(CommonResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .msg(ex.getBindingResult().getFieldError().getDefaultMessage())
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CommonResponse> handleValidationExceptions(IllegalArgumentException ex) {
        return ResponseEntity.ok().body(CommonResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .msg(ex.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResponse> handleException(Exception ex) {
        return ResponseEntity.ok().body(CommonResponse.builder()
                .statusCode(400)
                .msg(ex.getMessage())
                .build());
    }
}

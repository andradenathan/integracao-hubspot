package com.github.andradenathan.base.exceptions;

import com.github.andradenathan.base.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class MeetimeExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse("An error occurred: " + e.getMessage(), "error"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse> handleUnauthorizedException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new BaseResponse("An error occurred: " + e.getMessage(), "error"));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<BaseResponse> handleHttpClientErrorException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new BaseResponse("An error ocurred attempting to create contact: " + e.getMessage(), "error"));
    }
}

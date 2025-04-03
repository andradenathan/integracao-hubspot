package com.github.andradenathan.base;

public record BaseResponse(
        Object data,
        String message,
        String status
) {
    public BaseResponse(String message, String status) {
        this(null, message, status);
    }
}

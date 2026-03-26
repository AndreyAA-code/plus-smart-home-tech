package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    private final String reason;
    
    public ApiException(String reason, String message) {
        super(message);
        this.reason = reason;
    }
    
    public ApiException(String reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }
}
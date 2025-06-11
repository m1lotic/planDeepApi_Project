package com.planApiService.manage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LoginFailedException extends RuntimeException {

    private final HttpStatus status;

    public LoginFailedException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

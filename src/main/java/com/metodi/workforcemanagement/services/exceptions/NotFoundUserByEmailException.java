package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User with this email or password not found")
public class NotFoundUserByEmailException extends RuntimeException {

    public NotFoundUserByEmailException(String message) {
        super(message);
    }
}

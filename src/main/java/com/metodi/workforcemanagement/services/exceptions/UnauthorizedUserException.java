package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "The user is not authorized to access this resource")
public class UnauthorizedUserException extends RuntimeException{

    public UnauthorizedUserException(String message) {
        super(message);
    }
}

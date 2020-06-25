package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The user already sent his response for that request!" )
public class AlreadyExistsResponseException extends RuntimeException{
    public AlreadyExistsResponseException(String message) {
        super(message);
    }
}

package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Cannot make changes to a request if in status different from AWAITING")
public class UpdateNotPermittedException extends RuntimeException{
    public UpdateNotPermittedException(String message) {
        super(message);
    }
}

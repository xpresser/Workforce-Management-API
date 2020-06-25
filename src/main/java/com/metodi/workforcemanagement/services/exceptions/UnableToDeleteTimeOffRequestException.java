package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_MODIFIED)
public class UnableToDeleteTimeOffRequestException extends RuntimeException {

    public UnableToDeleteTimeOffRequestException(String message) {
        super(message);
    }
}

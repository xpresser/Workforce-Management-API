package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class NotAvailableTimeOffRequestException extends RuntimeException {

    public NotAvailableTimeOffRequestException(String message) {
        super(message);
    }
}

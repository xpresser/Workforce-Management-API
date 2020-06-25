package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_MODIFIED, reason = "Leave days exceed the amount of days left in your allowance")
public class LeaveDaysLimitExceededException extends RuntimeException {

    public LeaveDaysLimitExceededException(String message) {
        super(message);
    }
}

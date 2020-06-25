package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NotFoundUserByIdException extends RuntimeException {

    public NotFoundUserByIdException(Long id) {
        super(String.format("User with id: %d not found", id));
    }
}

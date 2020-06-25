package com.metodi.workforcemanagement.controllers.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor
public class ErrorResponse {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String fieldName;

    public ErrorResponse(Instant timestamp, Integer status, String error, String message, String fieldName) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldName = fieldName;
    }
}

package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY, reason = "Failed to send email" )
public class EmailServiceDownException extends RuntimeException{}

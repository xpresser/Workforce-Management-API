package com.metodi.workforcemanagement.configuration;

import com.metodi.workforcemanagement.controllers.dtos.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@ControllerAdvice
public class ControllerValidationHandler {

    private static final String BAD_REQUEST_ERROR = "Bad Request";
    private final MessageSource messageSource;

    @Autowired
    public ControllerValidationHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorResponse> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> errors = result.getFieldErrors();
        List<ErrorResponse> message = new ArrayList<>();
        errors.forEach(er -> message.add(processFieldError(er)));
        return message;
    }

    private ErrorResponse processFieldError(FieldError error) {
        ErrorResponse message = null;
        if (error != null) {
            Locale currentLocale = LocaleContextHolder.getLocale();
            String msg = messageSource.getMessage(
                    Objects.requireNonNull(error.getDefaultMessage())
                    , null, currentLocale);

            Instant timestamp = Instant.now();
            Integer status = HttpStatus.BAD_REQUEST.value();

            message = new ErrorResponse(timestamp,status, BAD_REQUEST_ERROR, msg, error.getField());
        }
        return message;
    }
}

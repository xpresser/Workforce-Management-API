package com.metodi.workforcemanagement.services.exceptions;

import com.metodi.workforcemanagement.services.exceptions.validators.UniqueEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "{com.metodi.workforcemanagement.services.exceptions.UniqueEmail.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
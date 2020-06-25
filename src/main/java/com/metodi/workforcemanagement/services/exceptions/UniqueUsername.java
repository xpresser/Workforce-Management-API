package com.metodi.workforcemanagement.services.exceptions;

import com.metodi.workforcemanagement.services.exceptions.validators.UniqueUsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
    String message() default "{com.metodi.workforcemanagement.services.exceptions.UniqueUsername.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
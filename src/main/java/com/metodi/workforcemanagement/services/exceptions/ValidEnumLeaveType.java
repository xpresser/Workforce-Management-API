package com.metodi.workforcemanagement.services.exceptions;

import com.metodi.workforcemanagement.services.exceptions.validators.TimeOffRequestLeaveTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeOffRequestLeaveTypeValidator.class)
public @interface ValidEnumLeaveType {
    String message() default "{com.metodi.workforcemanagement.services.exceptions.ValidEnumLeaveType.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Enum<?>> targetClassType();
}

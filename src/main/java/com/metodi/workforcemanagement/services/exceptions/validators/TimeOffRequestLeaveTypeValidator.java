package com.metodi.workforcemanagement.services.exceptions.validators;

import com.metodi.workforcemanagement.services.exceptions.ValidEnumLeaveType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeOffRequestLeaveTypeValidator implements ConstraintValidator<ValidEnumLeaveType, String> {

    private Set<String> LEAVE_TYPE_VALUES;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void initialize(ValidEnumLeaveType constraintAnnotation) {
        Class<? extends Enum> enumSelected = constraintAnnotation.targetClassType();
        LEAVE_TYPE_VALUES = (Set<String>) EnumSet.allOf(enumSelected)
                .stream()
                .map(e -> ((Enum<? extends Enum<?>>) e).name())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String leaveType, ConstraintValidatorContext constraintValidatorContext) {
        return LEAVE_TYPE_VALUES.contains(leaveType);
    }
}

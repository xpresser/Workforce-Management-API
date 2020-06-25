package com.metodi.workforcemanagement.controllers.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LeaveType {
    PAID_LEAVE(20,"PAID_LEAVE"),
    UNPAID_LEAVE(90, "UNPAID_LEAVE"),
    SICK_LEAVE(40, "SICK_LEAVE");

    private final Integer days;
    private final String leave;

    LeaveType(Integer days, String leave) {
        this.days = days;
        this.leave = leave;
    }

    public Integer getDays() {
        return this.days;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(leave);
    }
}

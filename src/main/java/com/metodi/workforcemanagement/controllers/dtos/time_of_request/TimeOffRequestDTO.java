package com.metodi.workforcemanagement.controllers.dtos.time_of_request;

import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.services.exceptions.ValidEnumLeaveType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter @Setter
public class TimeOffRequestDTO {

    @ValidEnumLeaveType(targetClassType = LeaveType.class,
            message = "error.request.type_off_leave.not_valid")
    @NotEmpty(message = "error.request.type_off_leave.empty")
    private String typeOfLeave;

    @NotNull(message = "error.request.start_date.empty")
    private LocalDate startDate;

    @NotNull(message = "error.request.end_date.empty")
    private LocalDate endDate;

    @NotBlank(message = "error.request.reason.empty")
    private String reason;
}

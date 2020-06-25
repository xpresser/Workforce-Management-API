package com.metodi.workforcemanagement.controllers.dtos.time_of_response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ApprovalRequestDTO {
    @JsonProperty("isApproved")
    @NotNull(message = "error.response.isApproved.empty")
    private boolean isApproved;
}

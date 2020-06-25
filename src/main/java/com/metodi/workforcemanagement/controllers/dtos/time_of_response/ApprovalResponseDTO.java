package com.metodi.workforcemanagement.controllers.dtos.time_of_response;

import com.metodi.workforcemanagement.controllers.dtos.time_of_request.RequestShortDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import lombok.Data;

@Data
public class ApprovalResponseDTO {

    private Long id;

    private RequestShortDTO request;

    private UserShortDTO approver;

    private boolean isApproved;
}

package com.metodi.workforcemanagement.controllers.dtos.time_of_response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShortApprovalResponseDTO {

    private Long id;

    private String approver;

    private boolean isApproved;
}

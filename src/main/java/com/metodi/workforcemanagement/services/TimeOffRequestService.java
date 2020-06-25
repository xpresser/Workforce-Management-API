package com.metodi.workforcemanagement.services;

import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestResponseDTO;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TimeOffRequestService {

    List<TimeOffRequestResponseDTO> getAll();

    TimeOffRequestResponseDTO getRequestById(long requestId);

    TimeOffRequest getById(long requestId);

    TimeOffRequestResponseDTO edit(long requestId, TimeOffRequestDTO timeOffRequestDTO);

    TimeOffRequestResponseDTO create(TimeOffRequestDTO timeOffRequestDTO, Long requesterId);

    Integer deleteById(long requestId);

    Page<TimeOffRequestResponseDTO> getAll(Pageable pageable);
}

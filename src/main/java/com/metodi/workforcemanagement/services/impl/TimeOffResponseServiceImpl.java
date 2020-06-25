package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.configuration.ModelMapperConfig;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalResponseDTO;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.TimeOffResponse;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.events.publishers.ResponseEventPublisher;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.TimeOffResponseRepository;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.TimeOffResponseService;
import com.metodi.workforcemanagement.services.exceptions.*;
import com.metodi.workforcemanagement.utils.TeamLeaderCheck;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TimeOffResponseServiceImpl implements TimeOffResponseService {

    private final TimeOffResponseRepository timeOffResponseRepository;

    private final ResponseEventPublisher  responseEventPublisher;

    private final ModelMapper modelMapper;

    private final AuthenticationService authenticationService;

    private final TeamLeaderCheck teamLeaderCheck;

    private final UserRepository userRepository;

    private final TimeOffRequestRepository timeOffRequestRepository;

    @Autowired
    public TimeOffResponseServiceImpl(TimeOffResponseRepository timeOffResponseRepository,
                                      ResponseEventPublisher responseEventPublisher,
                                      ModelMapperConfig modelMapper,
                                      AuthenticationService authenticationService,
                                      TeamLeaderCheck teamLeaderCheck,
                                      UserRepository userRepository,
                                      TimeOffRequestRepository timeOffRequestRepository) {

        this.timeOffResponseRepository = timeOffResponseRepository;
        this.responseEventPublisher = responseEventPublisher;
        this.modelMapper = modelMapper.getModelMapper();
        this.authenticationService = authenticationService;
        this.teamLeaderCheck = teamLeaderCheck;
        this.userRepository = userRepository;
        this.timeOffRequestRepository = timeOffRequestRepository;
    }

    @Override
    public List<ApprovalResponseDTO> getAll() {
        User loggedUser = authenticationService.getLoggedUser();
        List<TimeOffResponse> timeOffResponses = new ArrayList<>();

        if(loggedUser.isAdmin()) {
            timeOffResponses = timeOffResponseRepository.findAll();
        } else if(teamLeaderCheck.loggedUserIsTeamLead()) {
            timeOffResponses = timeOffResponseRepository.findAll().stream()
                    .filter(timeOffResponse -> timeOffResponse.getApprover() == loggedUser)
                    .collect(Collectors.toList());
        }
        return timeOffResponses.stream()
                .map(response -> modelMapper.map(response, ApprovalResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TimeOffResponse getById(Long responseId) {
        return timeOffResponseRepository.findById(responseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("TimeOffResponse with the following ID does not exist " + responseId));
    }

    @Override
    public ApprovalResponseDTO getResponseById(long responseId) {
        User loggedUser = authenticationService.getLoggedUser();
        TimeOffResponse timeOffResponse = getById(responseId);
        if(teamLeaderCheck.loggedUserIsTeamLead()) {
            if ( !timeOffResponse.getApprover().equals(loggedUser)) {
                throw new UnauthorizedUserException("The user is not authorized to access this resource!");
            }
        }
        return  modelMapper.map(timeOffResponse, ApprovalResponseDTO.class);
    }

    @Override
    public ApprovalResponseDTO create(Long timeOffRequestId , ApprovalRequestDTO approvalRequestDTO) {
        User approver;
        if ( authenticationService.userIsLoggedIn()) {
            approver = authenticationService.getLoggedUser();
        } else {
            approver = userRepository.findById(1L)
                    .orElseThrow(() -> new NotFoundUserByIdException(1L));
        }

        TimeOffRequest timeOffRequest = timeOffRequestRepository.findById(timeOffRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource does not exist."));
        checkIfRequestStatusIsAwaiting(timeOffRequest);
        checkIfUserAlreadySentResponse(timeOffRequest, approver);

        TimeOffResponse timeOffResponse= new TimeOffResponse();
        timeOffResponse.setApprover(approver);
        timeOffResponse.setRequest(timeOffRequest);
        timeOffResponse.setApproved(approvalRequestDTO.isApproved());

        responseEventPublisher.publishResponseCreatedEvent(timeOffResponse);

        return modelMapper.map(timeOffResponseRepository.save(timeOffResponse), ApprovalResponseDTO.class);
    }

    @Override
    public Page<ApprovalResponseDTO> getAll(Pageable pageable) {

        User loggedUser = this.authenticationService.getLoggedUser();
        Page<TimeOffResponse> responsesFromDatabase;
        if (loggedUser.isAdmin()) {

            responsesFromDatabase = this.timeOffResponseRepository
                    .findAll(pageable);
        } else {
            responsesFromDatabase = this.timeOffResponseRepository
                    .findAllByApproverId(pageable, loggedUser.getId());
        }

        long totalElements = responsesFromDatabase.getTotalElements();
        return new PageImpl<>(
                responsesFromDatabase.stream()
                        .map(u -> this.modelMapper.map(u, ApprovalResponseDTO.class))
                        .collect(Collectors.toList()), pageable, totalElements);
    }

    private void checkIfRequestStatusIsAwaiting(TimeOffRequest timeOffRequest) {
        if (!timeOffRequest.getStatus().equals(Status.AWAITING)) {
            throw new UpdateNotPermittedException("Cannot make changes to a request if in status different from AWAITING");
        }
    }

    private void checkIfUserAlreadySentResponse(TimeOffRequest timeOffRequest, User approver) {
        if (timeOffRequest.getResponses().stream().anyMatch(response -> response.getApprover().equals(approver)) && approver.getId() != 1L) {
            throw new AlreadyExistsResponseException("The user already sent his response for that request!");
        }
    }
}

package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.configuration.ModelMapperConfig;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestResponseDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.events.publishers.RequestEventPublisher;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.CalendarService;
import com.metodi.workforcemanagement.services.TimeOffRequestService;
import com.metodi.workforcemanagement.services.exceptions.*;
import com.metodi.workforcemanagement.utils.TeamLeaderCheck;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeOffRequestServiceImpl implements TimeOffRequestService {

    private static final int ZERO = 0;
    private static final int CANCELED = 2;

    private final TimeOffRequestRepository timeOffRequestRepository;

    private final UserRepository userRepository;

    private final RequestEventPublisher requestEventPublisher;

    private final ModelMapper modelMapper;

    private final AuthenticationService authenticationService;

    private final CalendarService calendarService;

    private final TeamLeaderCheck teamLeaderCheck;

    @Autowired
    public TimeOffRequestServiceImpl(TimeOffRequestRepository timeOffRequestRepository,
                                     UserRepository userRepository,
                                     RequestEventPublisher requestEventPublisher,
                                     ModelMapperConfig modelMapper,
                                     AuthenticationService authenticationService,
                                     CalendarService calendarService,
                                     TeamLeaderCheck teamLeaderCheck) {
        this.timeOffRequestRepository = timeOffRequestRepository;
        this.userRepository = userRepository;
        this.requestEventPublisher = requestEventPublisher;
        this.modelMapper = modelMapper.getModelMapper();
        this.authenticationService = authenticationService;
        this.calendarService = calendarService;
        this.teamLeaderCheck = teamLeaderCheck;
    }

    @Override
    public List<TimeOffRequestResponseDTO> getAll() {
        User loggedUser = authenticationService.getLoggedUser();

        List<TimeOffRequestResponseDTO> timeOffRequests = timeOffRequestRepository.findAll()
                .stream()
                .map(timeOffRequest -> modelMapper.map(timeOffRequest, TimeOffRequestResponseDTO.class))
                .collect(Collectors.toList());

        if (!loggedUser.isAdmin()) {
            return timeOffRequestRepository.findAllByRequesterId(loggedUser.getId())
                    .stream()
                    .map(timeOffRequest -> modelMapper.map(timeOffRequest, TimeOffRequestResponseDTO.class))
                    .collect(Collectors.toList());
        }

        return timeOffRequests;
    }

    @Override
    public TimeOffRequestResponseDTO getRequestById(long requestId) {
        return this.modelMapper.map(this.getById(requestId), TimeOffRequestResponseDTO.class);
    }

    @Override
    public TimeOffRequest getById(long requestId) {
        User loggedUser = authenticationService.getLoggedUser();

        TimeOffRequest timeOffRequest = timeOffRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Time Off Request not found in database"));

        checkIsUserAuthorized(loggedUser, timeOffRequest);

        return timeOffRequest;
    }

    @Override
    public TimeOffRequestResponseDTO edit(long requestId, TimeOffRequestDTO timeOffRequestDTO) {
        User loggedUser = authenticationService.getLoggedUser();
        TimeOffRequest editedTimeOffRequest = getById(requestId);
        Status requestStatus = editedTimeOffRequest.getStatus();

        if (!requestStatus.equals(Status.AWAITING) || editedTimeOffRequest.getResponses().size() > 0) {
            String reason = requestStatus.toString().toLowerCase();
            throw new NotAvailableTimeOffRequestException(String.format("Unable to change %s Time Off Request", reason));
        }

        if (!loggedUser.isAdmin() && !editedTimeOffRequest.getRequesterId().equals(loggedUser.getId())) {
            throw new UnauthorizedUserException("User is neither admin nor a requester");
        }

        editedTimeOffRequest.setLeaveType(LeaveType.valueOf(timeOffRequestDTO.getTypeOfLeave()));
        editedTimeOffRequest.setStartDate(timeOffRequestDTO.getStartDate());
        editedTimeOffRequest.setEndDate(timeOffRequestDTO.getEndDate());
        editedTimeOffRequest.setReason(timeOffRequestDTO.getReason());
        editedTimeOffRequest.setStatus(getRequestStatus(editedTimeOffRequest));

        int actualLeaveDays = getDesiredDaysOff(editedTimeOffRequest);

        hasEnoughLeaveDays(editedTimeOffRequest, actualLeaveDays, editedTimeOffRequest.getRequesterId());

        editedTimeOffRequest.setLeaveWorkDays(actualLeaveDays);

        requestEventPublisher.publishRequestCreatedEvent(editedTimeOffRequest);

        return modelMapper.map(timeOffRequestRepository.saveAndFlush(editedTimeOffRequest),
                TimeOffRequestResponseDTO.class);
    }

    @Override
    public TimeOffRequestResponseDTO create(TimeOffRequestDTO timeOffRequestDTO, Long requesterId) {
        TimeOffRequest requestForLeave = this.modelMapper.map(timeOffRequestDTO, TimeOffRequest.class);
        requestForLeave.setStatus(getRequestStatus(requestForLeave));

        int actualLeaveDays = getDesiredDaysOff(requestForLeave);

        hasEnoughLeaveDays(requestForLeave, actualLeaveDays, requesterId);

        requestForLeave.setLeaveWorkDays(actualLeaveDays);
        requestForLeave.setRequesterId(requesterId);

        User requester = this.userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundUserByIdException(requesterId));

        Boolean existAwaitingRequest = this.timeOffRequestRepository
                .existsByRequesterIdAndStatus(requesterId, Status.AWAITING);

        if (existAwaitingRequest) {
            throw new AlreadyExistRequestException();
        }

        if (ZERO == requester.getTeams().size()) {
            requestForLeave.setStatus(Status.APPROVED);
        }

        requestEventPublisher.publishRequestCreatedEvent(requestForLeave);

        return this.modelMapper.map(this.timeOffRequestRepository.save(requestForLeave),
                TimeOffRequestResponseDTO.class);
    }

    @Override
    @Transactional
    public Integer deleteById(long requestId) {
        User loggedUser = authenticationService.getLoggedUser();
        TimeOffRequest timeOffRequest = timeOffRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Time Off Request not found with ID: " + requestId));

        checkIsUserNotRequesterAndNotAdmin(loggedUser, timeOffRequest);

        if (timeOffRequest.getResponses().size() > 0) {
            requestEventPublisher.publicRequestCanceledEvent(timeOffRequest);
            return CANCELED;
        }

        return timeOffRequestRepository.removeById(requestId);
    }

    @Override
    public Page<TimeOffRequestResponseDTO> getAll(Pageable pageable) {
        User loggedUser = this.authenticationService.getLoggedUser();
        Page<TimeOffRequest> requestsFromDatabase;
        if (loggedUser.isAdmin()) {

            requestsFromDatabase = this.timeOffRequestRepository
                    .findAll(pageable);
        } else {
            requestsFromDatabase = this.timeOffRequestRepository
                    .findAllByRequesterId(pageable, loggedUser.getId());
        }

        long totalElements = requestsFromDatabase.getTotalElements();
        return new PageImpl<>(
                requestsFromDatabase.stream()
                        .map(u -> this.modelMapper.map(u, TimeOffRequestResponseDTO.class))
                        .collect(Collectors.toList()), pageable, totalElements);
    }

    private void hasEnoughLeaveDays(TimeOffRequest timeOffRequest, int desiredDaysOff, Long requesterId) {
        LeaveType leaveType = timeOffRequest.getLeaveType();
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundUserByIdException(requesterId));
        int daysLeft = requester.getRemainingDaysOff().get(leaveType);

        if (desiredDaysOff > daysLeft) {
            throw new LeaveDaysLimitExceededException("Leave days exceed the amount of days left in user allowance");
        }
    }

    private int getDesiredDaysOff(TimeOffRequest timeOffRequest) {
        LeaveType leaveType = timeOffRequest.getLeaveType();
        int desiredDaysOff;

        if (leaveType.equals(LeaveType.SICK_LEAVE)) {
            desiredDaysOff = (int) timeOffRequest.getStartDate().
                    datesUntil(timeOffRequest.getEndDate().plus(1, ChronoUnit.DAYS)).count();
        } else {
            desiredDaysOff = getLeaveWorkDays(timeOffRequest);
        }

        return desiredDaysOff;
    }

    private Status getRequestStatus(TimeOffRequest timeOffRequest) {

        return timeOffRequest
                .getLeaveType()
                .equals(LeaveType.SICK_LEAVE)
                ? Status.APPROVED
                : Status.AWAITING;
    }

    private void checkIsUserAuthorized(User loggedUser, TimeOffRequest timeOffRequest) {
        if (!loggedUser.getId().equals(timeOffRequest.getRequesterId()) &&
                !loggedUser.isAdmin() && !teamLeaderCheck.loggedUserIsTeamLead()) {
            throw new UnauthorizedUserException("User is not requester or admin or team lead");
        }
    }

    private void checkIsUserNotRequesterAndNotAdmin(User loggedUser, TimeOffRequest timeOffRequest) {
        if (!loggedUser.getId().equals(timeOffRequest.getRequesterId()) && !loggedUser.isAdmin()) {
            throw new UnauthorizedUserException("User is not admin or requester");
        }
    }

    private int getLeaveWorkDays(TimeOffRequest editedTimeOffRequest) {
        LocalDate startDate = editedTimeOffRequest.getStartDate();
        LocalDate endDate = editedTimeOffRequest.getEndDate();

        return this.calendarService.getNumberOfWorkingDays(startDate, endDate);
    }
}

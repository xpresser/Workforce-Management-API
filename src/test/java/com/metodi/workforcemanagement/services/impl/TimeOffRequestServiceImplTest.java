package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.configuration.ModelMapperConfig;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestResponseDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.TimeOffResponse;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.events.publishers.RequestEventPublisher;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.TimeOffRequestService;
import com.metodi.workforcemanagement.services.exceptions.LeaveDaysLimitExceededException;
import com.metodi.workforcemanagement.services.exceptions.NotAvailableTimeOffRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TimeOffRequestServiceImplTest {

    private static final Long REQUEST_ID = 1L;
    private static final Long USER_ID = 100L;

    private TimeOffRequest timeOffRequest;
    private ModelMapper modelMapper;
    private User adminUser;

    @MockBean
    TimeOffRequestRepository timeOffRequestRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    RequestEventPublisher requestEventPublisher;

    @Autowired
    ModelMapperConfig modelMapperConfig;

    @Autowired
    private TimeOffRequestService timeOffRequestService;

    @BeforeEach
    void setUp() {
        modelMapper = modelMapperConfig.getModelMapper();
        adminUser = createAdminUser();
        timeOffRequest = createTimeOffRequest();
    }

    @Test
    @DisplayName("Should return a list of all existing time off requests")
    void getAllAsAdmin() {
        List<TimeOffRequest> requests = new ArrayList<>();
        requests.add(timeOffRequest);

        when(authenticationService.getLoggedUser())
                .thenReturn(adminUser);
        when(timeOffRequestRepository.findAll())
                .thenReturn(requests);

        List<TimeOffRequestResponseDTO> expectedList = requests
                .stream().map(timeOffRequests -> modelMapper.map(timeOffRequests, TimeOffRequestResponseDTO.class))
                .collect(Collectors.toList());

        List<TimeOffRequestResponseDTO> actualList = timeOffRequestService.getAll();

        assertAll(
            () -> assertNotNull(actualList),
            () -> assertEquals(expectedList, actualList)
        );

        verify(authenticationService)
                .getLoggedUser();

        verify(timeOffRequestRepository)
                .findAll();
    }

    @Test
    @DisplayName("Should return json object of time off request with given id")
    void getTimeOffRequestById() {
        when(authenticationService.getLoggedUser())
                .thenReturn(adminUser);

        when(timeOffRequestRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(timeOffRequest));

        TimeOffRequest expectedRequest = modelMapper.map(timeOffRequest, TimeOffRequest.class);
        TimeOffRequest actualTimeOffRequest = timeOffRequestService.getById(REQUEST_ID);

        assertAll(
                () -> assertNotNull(expectedRequest),
                () -> assertEquals(expectedRequest, actualTimeOffRequest)
        );

        verify(authenticationService)
                .getLoggedUser();

        verify(timeOffRequestRepository)
                .findById(anyLong());
    }

    @Test
    @DisplayName("Should create time off request successfully")
    void create() {
        TimeOffRequestResponseDTO expectedRequest = modelMapper.map(timeOffRequest, TimeOffRequestResponseDTO.class);
        TimeOffRequestDTO timeOffRequestDTO = getTimeOffRequestDTO();
        when(userRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(adminUser));

        when(timeOffRequestRepository.existsByRequesterIdAndStatus(anyLong(), any(Status.class)))
                .thenReturn(false);
        when(timeOffRequestRepository.save(any())).thenReturn(timeOffRequest);

        TimeOffRequestResponseDTO actualRequest = timeOffRequestService.create(timeOffRequestDTO, timeOffRequest.getId());

        assertAll(
                () -> assertNotNull(actualRequest),
                () ->assertEquals(expectedRequest.getId(), actualRequest.getId())
        );

        verify(userRepository, times(4))
                .findById(anyLong());

        verify(timeOffRequestRepository)
                .existsByRequesterIdAndStatus(anyLong(), any(Status.class));
    }

    @Test
    @DisplayName("Should throw LeaveDaysLimitExceededException")
    void createWhenRequesterDoesntHaveEnoughDaysOffLeft() {
        TimeOffRequestDTO timeOffRequestDTO = getTimeOffRequestDTO();

        adminUser.getRemainingDaysOff().put(LeaveType.PAID_LEAVE,3);

        when(userRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(adminUser));

        Exception exception = assertThrows(LeaveDaysLimitExceededException.class,
                () -> timeOffRequestService.create(timeOffRequestDTO, timeOffRequest.getId()));

        String expectedMessage = "Leave days exceed the amount of days left in user allowance";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository)
                .findById(anyLong());
    }

    @Test
    @DisplayName("Should publish a RequestCreatedEvent")
    void publishRequestCreatedEventTest() {
        TimeOffRequestDTO timeOffRequestDTO = getTimeOffRequestDTO();

        when(userRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(adminUser));
        when(timeOffRequestRepository.save(any())).thenReturn(timeOffRequest);

        timeOffRequestService.create(timeOffRequestDTO, timeOffRequest.getId());

        verify(requestEventPublisher).publishRequestCreatedEvent(any(TimeOffRequest.class));
    }

    private TimeOffRequestDTO getTimeOffRequestDTO() {
        TimeOffRequestDTO timeOffRequestDTO = new TimeOffRequestDTO();
        timeOffRequestDTO.setTypeOfLeave(LeaveType.PAID_LEAVE.toString());
        timeOffRequestDTO.setStartDate(LocalDate.parse("2030-06-22"));
        timeOffRequestDTO.setEndDate(LocalDate.parse("2030-06-28"));
        timeOffRequestDTO.setReason("I want to rest!");
        return timeOffRequestDTO;
    }

    @Test
    @DisplayName("Should throw NotAvailableTimeOffRequestException")
    void editWhenStatusIsDifferentThanAWAITING() {
        TimeOffRequestDTO timeOffRequestDTO = getTimeOffRequestDTO();

        timeOffRequest.setStatus(Status.APPROVED);
        when(authenticationService.getLoggedUser())
                .thenReturn(adminUser);
        when(timeOffRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(timeOffRequest));

        Exception exception = assertThrows(NotAvailableTimeOffRequestException.class,
                () -> timeOffRequestService.edit(timeOffRequest.getId(), timeOffRequestDTO ));

        String expectedMessage = "Unable to change approved Time Off Request";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(authenticationService, times(2))
                .getLoggedUser();
        verify(timeOffRequestRepository)
                .findById(anyLong());
    }

    @Test
        @DisplayName("Should throw NotAvailableTimeOffRequestException if has responses already")
        void editWhenAlreadyHasResponses() {
            TimeOffRequestDTO timeOffRequestDTO = getTimeOffRequestDTO();

            timeOffRequest.getResponses().add(new TimeOffResponse());
            when(authenticationService.getLoggedUser())
                    .thenReturn(adminUser);
            when(timeOffRequestRepository.findById(anyLong()))
                    .thenReturn(Optional.of(timeOffRequest));

            Exception exception = assertThrows(NotAvailableTimeOffRequestException.class,
                    () -> timeOffRequestService.edit(timeOffRequest.getId(), timeOffRequestDTO ));

            String expectedMessage = "Unable to change awaiting Time Off Request";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

            verify(authenticationService, times(2))
                    .getLoggedUser();
            verify(timeOffRequestRepository)
                    .findById(anyLong());
    }

    @Test
    @DisplayName("Should publish ResponseCreatedEvent after editing a request.")
    void publishResponseCreatedEventAfterEditingTest() {
        TimeOffRequestDTO timeOffRequestDTO = getTimeOffRequestDTO();
        when(authenticationService.getLoggedUser())
                .thenReturn(adminUser);
        when(timeOffRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(timeOffRequest));
        when(userRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(adminUser));
        when(timeOffRequestRepository.saveAndFlush(any())).thenReturn(timeOffRequest);

        timeOffRequestService.edit(timeOffRequest.getId(), timeOffRequestDTO );

        verify(requestEventPublisher).publishRequestCreatedEvent(any(TimeOffRequest.class));
    }

    @Test
    @DisplayName("Should delete time off request successfully")
    void deleteById() {
        when(authenticationService.getLoggedUser())
                .thenReturn(adminUser);

        when(timeOffRequestRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(timeOffRequest));

        timeOffRequestService.deleteById(REQUEST_ID);

        verify(authenticationService)
                .getLoggedUser();

        verify(timeOffRequestRepository)
                .findById(anyLong());
    }

    @Test
    @DisplayName("Should publish RequestCanceledEvent when deleting request that already has responses.")
    void publishRequestCanceledEventWhenDeletingRequestWithResponses() {
        timeOffRequest.getResponses().add(new TimeOffResponse());
        when(authenticationService.getLoggedUser())
                .thenReturn(adminUser);

        when(timeOffRequestRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(timeOffRequest));

        timeOffRequestService.deleteById(REQUEST_ID);

        verify(requestEventPublisher)
                .publicRequestCanceledEvent(any(TimeOffRequest.class));
    }

    private User createAdminUser() {
        User user = new User();
        Map<LeaveType, Integer> remainingDaysOff = new HashMap<>();
        remainingDaysOff.put(LeaveType.PAID_LEAVE, 20);
        remainingDaysOff.put(LeaveType.UNPAID_LEAVE, 90);
        remainingDaysOff.put(LeaveType.SICK_LEAVE, 40);

        user.setId(USER_ID);
        user.setEmail("admin@gmail.com");
        user.setUsername("admin");
        user.setPassword("adminpass");
        user.setFirstName("admin");
        user.setLastName("administrator");
        user.setAdmin(true);
        user.setOnLeave(false);
        user.setCreatedAt(Instant.parse("2020-04-23T20:20:20.00Z"));
        user.setCreatedBy(USER_ID);
        user.setUpdatedAt(Instant.parse("2020-04-23T20:20:20.00Z"));
        user.setUpdatedBy(USER_ID);
        user.setRemainingDaysOff(remainingDaysOff);

        return user;
    }

    private TimeOffRequest createTimeOffRequest() {
        TimeOffRequest request = new TimeOffRequest();

        request.setId(REQUEST_ID);
        request.setStatus(Status.AWAITING);
        request.setRequesterId(USER_ID);
        request.setReason("I want some time off");
        request.setLeaveType(LeaveType.PAID_LEAVE);
        request.setStartDate(LocalDate.parse("2030-06-22"));
        request.setEndDate(LocalDate.parse("2030-06-28"));

        return request;
    }
}
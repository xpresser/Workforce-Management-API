package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.configuration.ModelMapperConfig;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalResponseDTO;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.TimeOffResponse;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.events.publishers.RequestEventPublisher;
import com.metodi.workforcemanagement.events.publishers.ResponseEventPublisher;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.TimeOffResponseRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.TimeOffResponseService;
import com.metodi.workforcemanagement.services.exceptions.AlreadyExistsResponseException;
import com.metodi.workforcemanagement.services.exceptions.UnauthorizedUserException;
import com.metodi.workforcemanagement.services.exceptions.UpdateNotPermittedException;
import com.metodi.workforcemanagement.utils.TeamLeaderCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TimeOffResponseServiceImplTest {

    @MockBean
    private TimeOffResponseRepository timeOffResponseRepository;

    @MockBean
    private TimeOffRequestRepository timeOffRequestRepository;

    @MockBean
    private ResponseEventPublisher responseEventPublisher;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TeamLeaderCheck teamLeaderCheck;

    @MockBean
    private RequestEventPublisher requestEventPublisher;

    @Autowired
    private TimeOffResponseService timeOffResponseService;

    @Autowired
    ModelMapperConfig modelMapperConfig;

    private ModelMapper modelMapper;
    private User testUserOne;
    private User testUserTwo;
    private User adminUser;
    private TimeOffResponse responseFromUserOne;
    private TimeOffResponse responseFromUserTwo;
    private TimeOffRequest timeOffRequest;

    @BeforeEach
    void setup() {
        modelMapper = modelMapperConfig.getModelMapper();
        adminUser = getAdminUser();
        testUserOne = getTestUserOne();
        testUserTwo = getTestUserTwo();

        timeOffRequest = new TimeOffRequest();
        timeOffRequest.setId(100L);
        timeOffRequest.setStatus(Status.AWAITING);

        responseFromUserOne = new TimeOffResponse();
        responseFromUserOne.setId(120L);
        responseFromUserOne.setApprover(testUserOne);
        responseFromUserOne.setApproved(true);
        responseFromUserOne.setRequest(timeOffRequest);

        responseFromUserTwo = new TimeOffResponse();
        responseFromUserTwo.setId(121L);
        responseFromUserTwo.setApprover(testUserTwo);
        responseFromUserTwo.setApproved(true);
        responseFromUserTwo.setRequest(timeOffRequest);
    }

    @Test
    void getAllAsAdmin() {
        List<TimeOffResponse> timeOffResponses = new ArrayList<>();
        timeOffResponses.add(responseFromUserOne);
        timeOffResponses.add(responseFromUserTwo);

        when(authenticationService.getLoggedUser())
                .thenReturn(adminUser);
        when(timeOffResponseRepository.findAll())
                .thenReturn(timeOffResponses);

        List<ApprovalResponseDTO> expectedList = timeOffResponses.stream().map(response -> modelMapper.map(response, ApprovalResponseDTO.class))
                .collect(Collectors.toList());

        List<ApprovalResponseDTO> actualList = timeOffResponseService.getAll();

        assertAll(
                () -> assertNotNull(actualList),
                () -> assertEquals(expectedList, actualList)
        );
        verify(authenticationService)
                .getLoggedUser();
        verify(timeOffResponseRepository)
                .findAll();
    }

    @Test
    void getAllAsTeamLeader() {
        List<TimeOffResponse> timeOffResponses = new ArrayList<>();
        timeOffResponses.add(responseFromUserOne);
        when(authenticationService.getLoggedUser())
                .thenReturn(testUserOne);
        when(timeOffResponseRepository.findAll())
                .thenReturn(timeOffResponses);
        when(teamLeaderCheck.loggedUserIsTeamLead())
                .thenReturn(true);

        List<ApprovalResponseDTO> expectedList = timeOffResponses.stream().map(response -> modelMapper.map(response, ApprovalResponseDTO.class))
                .collect(Collectors.toList());

        List<ApprovalResponseDTO> actualList = timeOffResponseService.getAll();

        assertAll(
                () -> assertNotNull(actualList),
                () -> assertEquals(expectedList, actualList)
        );
        verify(authenticationService)
                .getLoggedUser();
        verify(timeOffResponseRepository)
                .findAll();
        verify(teamLeaderCheck)
                .loggedUserIsTeamLead();
    }

    @Test
    void getResponseByIdAsAdmin() {
        when(authenticationService.getLoggedUser())
                .thenReturn(adminUser);
        when(timeOffResponseRepository.findById(responseFromUserOne.getId()))
                .thenReturn(Optional.of(responseFromUserOne));
        when(teamLeaderCheck.loggedUserIsTeamLead())
                .thenReturn(false);

        ApprovalResponseDTO expectedResponse = modelMapper.map(responseFromUserOne, ApprovalResponseDTO.class);

        ApprovalResponseDTO actualResponse = timeOffResponseService.getResponseById(responseFromUserOne.getId());

        assertAll(
                () -> assertEquals(expectedResponse, actualResponse)
        );
        verify(authenticationService)
                .getLoggedUser();
        verify(timeOffResponseRepository)
                .findById(anyLong());
        verify(teamLeaderCheck)
                .loggedUserIsTeamLead();
    }

    @Test
    void getResponseByIdAsTeamLeadTryingTOGetAResponseThatIsNotHis() {
        when(authenticationService.getLoggedUser())
                .thenReturn(testUserTwo);
        when(timeOffResponseRepository.findById(responseFromUserOne.getId()))
                .thenReturn(Optional.of(responseFromUserOne));
        when(teamLeaderCheck.loggedUserIsTeamLead())
                .thenReturn(true);

        Exception exception = assertThrows(UnauthorizedUserException.class, () -> {
            timeOffResponseService.getResponseById(responseFromUserOne.getId());
        });

        String expectedMessage = "The user is not authorized to access this resource!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createWhenRequestStatusIsNotAwaiting() {
        timeOffRequest.setStatus(Status.APPROVED);
        ApprovalRequestDTO approvalRequestDTO = new ApprovalRequestDTO();
        approvalRequestDTO.setApproved(true);

        when(authenticationService.getLoggedUser())
                .thenReturn(testUserOne);
        when(timeOffRequestRepository.findById(timeOffRequest.getId()))
                .thenReturn(Optional.of(timeOffRequest));

        Exception exception = assertThrows(UpdateNotPermittedException.class, () -> {
            timeOffResponseService.create(timeOffRequest.getId(),approvalRequestDTO);
        });

        String expectedMessage = "Cannot make changes to a request if in status different from AWAITING";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createWhenUserAlreadySentAResponse() {
        ApprovalRequestDTO approvalRequestDTO = new ApprovalRequestDTO();
        approvalRequestDTO.setApproved(true);
        timeOffRequest.getResponses().add(responseFromUserOne);

        when(authenticationService.userIsLoggedIn())
                .thenReturn(true);
        when(authenticationService.getLoggedUser())
                .thenReturn(testUserOne);
        when(timeOffRequestRepository.findById(timeOffRequest.getId()))
                .thenReturn(Optional.of(timeOffRequest));

        Exception exception = assertThrows(AlreadyExistsResponseException.class, () -> {
            timeOffResponseService.create(timeOffRequest.getId(),approvalRequestDTO);
        });

        String expectedMessage = "The user already sent his response for that request!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    private User getAdminUser() {
        User loggedUser = new User();
        loggedUser.setId(100L);
        loggedUser.setEmail("admin@abv.bg");
        loggedUser.setAdmin(true);
        loggedUser.setUsername("admin");

        return loggedUser;
    }

    private User getTestUserOne() {
        User loggedUser = new User();
        loggedUser.setId(101L);
        loggedUser.setEmail("testUserOne@abv.bg");
        loggedUser.setAdmin(false);
        loggedUser.setUsername("testUserOne");

        return loggedUser;
    }

    private User getTestUserTwo() {
        User loggedUser = new User();
        loggedUser.setId(102L);
        loggedUser.setEmail("testUserTwo@abv.bg");
        loggedUser.setAdmin(false);
        loggedUser.setUsername("testUserTwo");

        return loggedUser;
    }
}
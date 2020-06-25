package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.time_of_request.RequestShortDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.Team;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.TeamRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.TimeOffRequestService;
import com.metodi.workforcemanagement.services.TimeOffResponseService;
import com.metodi.workforcemanagement.services.UserService;
import com.metodi.workforcemanagement.services.impl.JWTTokenServiceImpl;
import static com.metodi.workforcemanagement.test_resources.TimeOffResponseJSON.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@WithMockUser(roles = "ADMIN")
class TimeOffResponseControllerTest {

    @MockBean
    private TimeOffResponseService timeOffResponseService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TeamRepository teamRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private JWTTokenServiceImpl tokenService;

    @MockBean
    private TimeOffRequestService timeOffRequestService;

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    private ApprovalResponseDTO approvalResponseDTO;

    private String token;

    private User loggedUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RequestShortDTO request = new RequestShortDTO();
        request.setId(109L);
        request.setLeaveType(LeaveType.PAID_LEAVE);
        request.setStatus(Status.AWAITING);
        request.setRequester("testUser");

        UserShortDTO user = new UserShortDTO();
        user.setId(100L);
        user.setUsername("Administrator");

        token = "some string";
        approvalResponseDTO = new ApprovalResponseDTO();
        approvalResponseDTO.setId(100L);
        approvalResponseDTO.setRequest(request);
        approvalResponseDTO.setApprover(user);
        approvalResponseDTO.setApproved(true);

        loggedUser = new User();
        loggedUser.setId(100L);
        loggedUser.setAdmin(true);
    }

    @Test
    void getAll() throws Exception {
        List<ApprovalResponseDTO> responses = new ArrayList<>();
        responses.add(approvalResponseDTO);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ApprovalResponseDTO> page = new PageImpl<>(responses, pageable, responses.size());

        when(timeOffResponseService.getAll(any()))
                .thenReturn(page);
        when(authenticationService.getLoggedUser())
                .thenReturn(loggedUser);
        when(teamRepository.existsByTeamLeaderId(anyLong()))
                .thenReturn(true);

        mockMvc.perform(get("/time-off-responses")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json( GET_ALL_RESPONSES_JSON));

        verify(timeOffResponseService)
                .getAll(any());
        verify(authenticationService)
                .getLoggedUser();
        verify(teamRepository)
                .existsByTeamLeaderId(anyLong());
    }

    @Test
    void getById() throws Exception {
        when(timeOffResponseService.getResponseById(anyLong()))
                .thenReturn(approvalResponseDTO);
        when(authenticationService.getLoggedUser())
                .thenReturn(loggedUser);
        when(teamRepository.existsByTeamLeaderId(anyLong()))
                .thenReturn(true);

        mockMvc.perform(get("/time-off-responses/{responseId}", 100L)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(APPROVAL_RESPONSE_JSON));

        verify(timeOffResponseService)
                .getResponseById(anyLong());
        verify(authenticationService)
                .getLoggedUser();
        verify(teamRepository)
                .existsByTeamLeaderId(anyLong());
    }

    @Test
    void create() throws Exception {
        TimeOffRequest timeOffRequest = new TimeOffRequest();
        timeOffRequest.setId(100L);
        timeOffRequest.setRequesterId(3L);
        timeOffRequest.setStatus(Status.AWAITING);

        Team testTeam = new Team();
        testTeam.setTeamLeader(loggedUser);
        Set<Team> testTeams = new HashSet<>();
        testTeams.add(testTeam);
        loggedUser.setTeams(testTeams);
        when(authenticationService.getLoggedUser())
                .thenReturn(loggedUser);
        when(timeOffRequestService.getById(anyLong()))
                .thenReturn(timeOffRequest);
        when(userService.getById(anyLong()))
                .thenReturn(loggedUser);
        when(timeOffResponseService.create(anyLong(), any(ApprovalRequestDTO.class)))
                .thenReturn(approvalResponseDTO);

        mockMvc.perform( MockMvcRequestBuilders
                .post("/time-off-responses/for-request/{requestId}", 100L)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(    "{\"isApproved\": true}"
                )
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json( APPROVAL_RESPONSE_JSON));

        verify(authenticationService)
                .getLoggedUser();
        verify(timeOffRequestService)
                .getById(anyLong());
        verify(userService)
                .getById(anyLong());
        verify(timeOffResponseService)
                .create(anyLong(), any(ApprovalRequestDTO.class));
    }
}
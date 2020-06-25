package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ShortApprovalResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.TimeOffRequestService;
import com.metodi.workforcemanagement.services.impl.JWTTokenServiceImpl;
import com.metodi.workforcemanagement.utils.CheckUserIsAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.metodi.workforcemanagement.test_resources.TimeOffRequestJSON.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@WithMockUser(roles = "ADMIN")
class TimeOffRequestControllerTest {

    private TimeOffRequestResponseDTO testTimeOffRequest;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private JWTTokenServiceImpl tokenService;

    @MockBean
    private TimeOffRequestService timeOffRequestService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CheckUserIsAdmin checkUserIsAdmin;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        UserShortDTO adminUser = new UserShortDTO();
        adminUser.setId(ADMIN_USER_ID);
        adminUser.setUsername("admin");
        List<ShortApprovalResponseDTO> responsesList = new ArrayList<>();
        ShortApprovalResponseDTO response = createShortApprovalResponse();
        response.setId(RESPONSE_ID);
        response.setApprover(RESPONSE_APPROVER);
        response.setApproved(true);
        responsesList.add(response);

        testTimeOffRequest = new TimeOffRequestResponseDTO();
        testTimeOffRequest.setId(TIME_OFF_REQUEST_ID);
        testTimeOffRequest.setLeaveType(LeaveType.PAID_LEAVE);
        testTimeOffRequest.setStatus(Status.AWAITING);
        testTimeOffRequest.setRequester(adminUser);
        testTimeOffRequest.setStartDate(LocalDate.parse(START_DATE));
        testTimeOffRequest.setEndDate(LocalDate.parse(END_DATE));
        testTimeOffRequest.setReason(TIME_OFF_REQUEST_REASON);
        testTimeOffRequest.setLeaveWorkDays(5);
        testTimeOffRequest.setResponses(responsesList);
        testTimeOffRequest.setCreatedAt(Instant.parse(CREATED_AT));
        testTimeOffRequest.setCreatedBy(adminUser);
        testTimeOffRequest.setUpdatedAt(Instant.parse(CREATED_AT));
        testTimeOffRequest.setUpdatedBy(adminUser);
    }

    private ShortApprovalResponseDTO createShortApprovalResponse() {
        ShortApprovalResponseDTO response = new ShortApprovalResponseDTO();
        response.setId(RESPONSE_ID);
        response.setApprover("Approver");
        response.setApproved(true);
        return response;
    }

    @DisplayName("Get All should return json list of all existing time off requests")
    @Test
    void getAll() throws Exception {
        List<TimeOffRequestResponseDTO> timeOffRequests = new ArrayList<>();
        timeOffRequests.add(testTimeOffRequest);
        Pageable pageable = PageRequest.of(0, 10);
        Page<TimeOffRequestResponseDTO> page = new PageImpl<>(timeOffRequests, pageable, timeOffRequests.size());

        when(timeOffRequestService.getAll(any()))
                .thenReturn(page);

        mockMvc.perform(get("/time-off-request")
                .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(PAGEABLE_GET_ALL_JSON));

        verify(timeOffRequestService)
                .getAll(any());
    }

    @DisplayName("Get by ID should return the time off request with the specified ID in json format")
    @Test
    void getById() throws Exception {

        when(timeOffRequestService.getRequestById(TIME_OFF_REQUEST_ID))
                .thenReturn(testTimeOffRequest);

        mockMvc.perform(get("/time-off-request/{timeOffRequestId}", TIME_OFF_REQUEST_ID)
                .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(TIME_OFF_REQUEST_JSON));
        
        verify(timeOffRequestService)
                .getRequestById(TIME_OFF_REQUEST_ID);
    }

    @DisplayName("Create should return the time off request just created in json format")
    @Test
    void create() throws Exception {

        when(checkUserIsAdmin.isUserAdmin(anyLong()))
                .thenReturn(true);

        when(timeOffRequestService.create(any(TimeOffRequestDTO.class), anyLong()))
                .thenReturn(testTimeOffRequest);

        mockMvc.perform(post("/time-off-request/users/{userID}", ADMIN_USER_ID)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TIME_OFF_REQUEST_CREATE_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(TIME_OFF_REQUEST_JSON));

        verify(timeOffRequestService)
                .create(any(), anyLong());
    }

    @DisplayName("Edit should return the time off request after edit in json format")
    @Test
    void edit() throws Exception {

        when(timeOffRequestService.edit(anyLong(), any()))
                .thenReturn(testTimeOffRequest);

        mockMvc.perform(put("/time-off-request/{timeOffRequestId}", TIME_OFF_REQUEST_ID)
                .header("Authorization", TOKEN)
                .content(TIME_OFF_REQUEST_EDIT_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(TIME_OFF_REQUEST_JSON));

        verify(timeOffRequestService)
                .edit(anyLong(), any());
    }

    @DisplayName("Delete should return status No Content if Time Off Request is successfully deleted")
    @Test
    void deleteTimeOffRequest() throws Exception {

        when(timeOffRequestService.deleteById(TIME_OFF_REQUEST_ID))
                .thenReturn(1);

        mockMvc.perform(delete("/time-off-request/{timeOffRequestId}", TIME_OFF_REQUEST_ID)
                .header("Authorization", TOKEN))
                .andExpect(status().isOk());

        verify(timeOffRequestService)
                .deleteById(TIME_OFF_REQUEST_ID);
    }
}
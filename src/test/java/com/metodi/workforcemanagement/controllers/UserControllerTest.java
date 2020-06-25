package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.user.UserResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.UserService;
import com.metodi.workforcemanagement.services.impl.JWTTokenServiceImpl;
import com.metodi.workforcemanagement.utils.UniqueEmailCheck;
import com.metodi.workforcemanagement.utils.UniqueUsernameCheck;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.metodi.workforcemanagement.test_resources.UserJSON.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@WithMockUser(roles = "ADMIN")
class UserControllerTest {

    private UserResponseDTO testUser;
    private String token;
    private final long userId = 1;
    private UserShortDTO adminUser;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private JWTTokenServiceImpl tokenService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    UniqueUsernameCheck uniqueUsernameCheck;

    @MockBean
    UniqueEmailCheck uniqueEmailCheck;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        adminUser = new UserShortDTO();
        adminUser.setId(userId);
        adminUser.setUsername("admin");

        token = "some string";
        Map<LeaveType, Integer> remainingDaysOff = new HashMap<>();
        testUser = new UserResponseDTO();
        testUser.setId(userId);
        testUser.setEmail("admin@gmail.com");
        testUser.setRemainingDaysOff(remainingDaysOff);
        testUser.setUsername("admin");
        testUser.setFirstName("admin");
        testUser.setLastName("administrator");
        testUser.setAdmin(false);
        testUser.setOnLeave(false);
        testUser.setCreatedAt(Instant.parse("2020-05-23T20:20:20.00Z"));
        testUser.setCreatedBy(adminUser);
        testUser.setUpdatedAt(Instant.parse("2020-05-23T20:20:20.00Z"));
        testUser.setUpdatedBy(adminUser);
    }

    @DisplayName("Get All should return json list of all existing users")
    @Test
    void getAll() throws Exception{
        List<UserResponseDTO> usersList = new ArrayList<>();
        usersList.add(testUser);

        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> page = new PageImpl<>(usersList, pageable, usersList.size());
        when(userService.getAll(any()))
                .thenReturn(page);

        mockMvc.perform(get("/users")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(GET_ALL_USERS_JSON));

        verify(userService)
                .getAll(any());
    }

    @DisplayName("Get by ID should return the user with the specified ID in json format")
    @Test
    void getById() throws Exception {

        when(userService.getUserById(userId))
                .thenReturn(testUser);

        mockMvc.perform(get("/users/{userId}", userId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(USER_RESPONSE_JSON));

        verify(userService)
                .getUserById(userId);
    }

    @DisplayName("createUser should return the user just created in json format.")
    @Test
    void createUser() throws Exception {

        when(userService.createUser(any()))
                .thenReturn(testUser);

        when(userRepo.existsByEmail(anyString()))
                .thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(USER_REQUEST_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json( USER_RESPONSE_JSON));

        verify(userService)
                .createUser(any());
    }

    @DisplayName("Edit should return the user after edit in json format.")
    @Test
    void editUser() throws Exception {
      when(uniqueEmailCheck.isUniqueEmail(anyString(), anyLong()))
              .thenReturn(true);
      when(uniqueUsernameCheck.isUniqueUsername(anyString(), anyLong()))
              .thenReturn(true);
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(createEditUser());

        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/{userId}", userId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(EDIT_USER_REQUEST_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(EDIT_USER_RESPONSE_JSON));

        verify(userService)
                .updateUser(anyLong(), any());
    }

    @DisplayName("Delete should return status Ok if user is successfully deleted.")
    @Test
    void deleteUser() throws Exception {
        when(userService.deleteById(anyLong()))
                .thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/{userId}", userId)
                .header("Authorization", token))
                .andExpect(status().isOk());

        verify(userService)
        .deleteById(anyLong());
    }

    private UserResponseDTO createEditUser(){
        Map<LeaveType, Integer> remainingDaysOff = new HashMap<>();
        UserResponseDTO editUser = new UserResponseDTO();
        editUser.setId(userId);
        editUser.setEmail("editAdmin@gmail.com");
        editUser.setRemainingDaysOff(remainingDaysOff);
        editUser.setUsername("editAdmin");
        editUser.setFirstName("editAdmin");
        editUser.setLastName("editAdministrator");
        editUser.setAdmin(false);
        editUser.setOnLeave(false);
        editUser.setCreatedAt(Instant.parse("2020-05-23T20:20:20.00Z"));
        editUser.setCreatedBy(adminUser);
        editUser.setUpdatedAt(Instant.parse("2020-05-23T20:20:20.00Z"));
        editUser.setUpdatedBy(adminUser);

        return editUser;
    }
}

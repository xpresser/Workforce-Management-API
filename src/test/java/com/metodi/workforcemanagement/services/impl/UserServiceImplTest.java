package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.controllers.dtos.user.UserRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.LeftUserRepository;
import com.metodi.workforcemanagement.repositories.TeamRepository;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {

    public static final long USER_ID = 1L;
    private final long testUserId = 1;
    private User expectedUser;
    private UserResponseDTO userResponseDTO;
    private UserShortDTO adminUser;

    @MockBean
    TeamRepository teamRepository;

    @MockBean
    TimeOffRequestRepository timeOffRequestRepository;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private LeftUserRepository leftUserRepository;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        expectedUser = createTestUser();
        adminUser = new UserShortDTO();
        adminUser.setId(USER_ID);
    }


    @Test
    @DisplayName("Should return a list of all existing users.")
    void getAll() {
        List<User> userList = new ArrayList<>();
        userList.add(expectedUser);

        when(userRepo.findAll())
                .thenReturn(userList);

        List<UserResponseDTO> expectedUserList = userList.stream()
                .map(this::createUserDTO)
                .collect(Collectors.toList());

        List<UserResponseDTO> actualUserList = userService.getAll();

        assertAll(
                () -> assertNotNull(actualUserList),
                () -> assertEquals(expectedUserList.contains(createUserDTO(expectedUser)),
                        actualUserList.contains(createUserDTO(expectedUser))));

        verify(userRepo)
                .findAll();

    }

    @Test
    @DisplayName("Should return a user with the given ID.")
    void getUserById() {
        when(userRepo.findById(any(Long.class)))
                .thenReturn(java.util.Optional.ofNullable(expectedUser));

        userResponseDTO = createUserDTO(expectedUser);

        UserResponseDTO actualUser = userService.getUserById(testUserId);

        assertAll(
                () -> assertNotNull(actualUser),
                () -> assertEquals(userResponseDTO.getId(), actualUser.getId())
        );

        verify(userRepo, times(3))
                .findById(any(Long.class));
    }

    @Test
    @DisplayName("Should edit user successfully and return it.")
    void editUser() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("editAdmin@gmail.com");
        userRequestDTO.setUsername("editAdmin");
        userRequestDTO.setPassword("editAdminpass");
        userRequestDTO.setFirstName("admin");
        userRequestDTO.setLastName("administrator");
        userRequestDTO.setAdmin(false);
        userRequestDTO.setOnLeave(false);

        when(userRepo.findById(anyLong()))
                .thenReturn(java.util.Optional.of(expectedUser));

        when(userRepo.saveAndFlush(any(User.class)))
                .thenReturn(expectedUser);

        userResponseDTO = createUserDTO(expectedUser);

        UserResponseDTO actualUser = userService.updateUser(testUserId, userRequestDTO);

        assertAll(
                () -> assertNotNull(actualUser),
                () -> assertEquals(userResponseDTO.getId(), actualUser.getId())
        );

        verify(userRepo)
                .saveAndFlush(any(User.class));

        verify(userRepo, times(3))
                .findById(any(Long.class));

    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser() {
        UserRequestDTO userRequestDTO = createUserRequestDTO();

        when(userRepo.save(any(User.class)))
                .thenReturn(expectedUser);

        userResponseDTO = createUserDTO(expectedUser);
        UserResponseDTO actualUser = userService.createUser(userRequestDTO);

        assertAll(
                () -> assertNotNull(actualUser),
                () -> assertEquals(userResponseDTO.getId(), actualUser.getId())
        );
        verify(userRepo)
                .save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteById() {
        when(userRepo.userIsTeamLeader(anyLong()))
                .thenReturn(0);
        when(userRepo.userIsUsersCreator(anyLong()))
                .thenReturn(0);
        when(teamRepository.isTeamCreator(anyLong()))
                .thenReturn(0);
        when(timeOffRequestRepository.isRequestCreator(anyLong()))
                .thenReturn(0);
        when(timeOffRequestRepository.findAllByRequesterId(anyLong()))
                .thenReturn(new ArrayList<>());
        when(userRepo.findById(anyLong()))
                .thenReturn(Optional.of(expectedUser));
        when(userRepo.removeById(anyLong()))
                .thenReturn(1);

        userService.deleteById(testUserId);

        verify(userRepo)
                .userIsTeamLeader(anyLong());
        verify(userRepo)
                .userIsUsersCreator(anyLong());
        verify(teamRepository)
                .isTeamCreator(anyLong());
        verify(timeOffRequestRepository)
                .isRequestCreator(anyLong());
        verify(timeOffRequestRepository)
                .findAllByRequesterId(anyLong());
        verify(userRepo)
                .findById(anyLong());
        verify(leftUserRepository)
                .save(any());
        verify(userRepo)
                .removeById(any(Long.class));
    }

    @DisplayName("Should return true if user is a users creator")
    @Test
    void isUsersCreator() {
        when(this.userRepo.userIsUsersCreator(1L)).thenReturn(1);

        Boolean isCreator = this.userService.isUsersCreator(USER_ID);

        assertAll(
                () -> assertNotNull(isCreator),
                () -> assertEquals(Boolean.TRUE, isCreator)
        );

        verify(this.userRepo).userIsUsersCreator(any(Long.class));
    }

    @DisplayName("Should return true if user is a teams creator")
    @Test
    void isTeamsCreator() {
        when(this.teamRepository.isTeamCreator(1L)).thenReturn(1);

        Boolean isCreator = this.teamRepository.isTeamCreator(USER_ID) > 0;

        assertAll(
                () -> assertNotNull(isCreator),
                () -> assertEquals(Boolean.TRUE, isCreator)
        );

        verify(this.teamRepository).isTeamCreator(any(Long.class));
    }

    @DisplayName("Should return true if user is a requests creator")
    @Test
    void isRequestsCreator() {
        when(this.timeOffRequestRepository.isRequestCreator(1L)).thenReturn(1);

        Boolean isCreator = this.timeOffRequestRepository.isRequestCreator(USER_ID) > 0;

        assertAll(
                () -> assertNotNull(isCreator),
                () -> assertEquals(Boolean.TRUE, isCreator)
        );

        verify(this.timeOffRequestRepository).isRequestCreator(any(Long.class));
    }

    @DisplayName("Should return true if user is a team leader")
    @Test
    void isTeamLeader() {
        when(this.userRepo.userIsTeamLeader(1L)).thenReturn(1);

        Boolean isCreator = this.userRepo.userIsTeamLeader(USER_ID) > 0;

        assertAll(
                () -> assertNotNull(isCreator),
                () -> assertEquals(Boolean.TRUE, isCreator)
        );

        verify(this.userRepo).userIsTeamLeader(any(Long.class));
    }

    private UserRequestDTO createUserRequestDTO() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("admin@gmail.com");
        userRequestDTO.setUsername("admin");
        userRequestDTO.setPassword("adminpass");
        userRequestDTO.setFirstName("admin");
        userRequestDTO.setLastName("administrator");
        userRequestDTO.setAdmin(false);
        return userRequestDTO;
    }

    private User createTestUser() {
        User testUser = new User();
        Map<LeaveType, Integer> remainingDaysOff = new HashMap<>();
        testUser.setId(testUserId);
        testUser.setUsername("admin");
        testUser.setEmail("admin@gmail.com");
        testUser.setFirstName("admin");
        testUser.setLastName("administrator");
        testUser.setAdmin(false);
        testUser.setOnLeave(false);
        testUser.setCreatedAt(Instant.parse("2020-04-23T20:20:20.00Z"));
        testUser.setCreatedBy(testUserId);
        testUser.setUpdatedAt(Instant.parse("2020-04-23T20:20:20.00Z"));
        testUser.setUpdatedBy(testUserId);
        testUser.setRemainingDaysOff(remainingDaysOff);
        testUser.setPassword("adminpass");
        return testUser;
    }

    private UserResponseDTO createUserDTO(User user) {
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setAdmin(user.isAdmin());
        userResponseDTO.setOnLeave(user.isOnLeave());
        userResponseDTO.setCreatedAt(user.getCreatedAt());
        userResponseDTO.setCreatedBy(adminUser);
        userResponseDTO.setUpdatedBy(adminUser);
        userResponseDTO.setUpdatedAt(user.getUpdatedAt());
        userResponseDTO.setRemainingDaysOff(user.getRemainingDaysOff());
        return userResponseDTO;
    }
}

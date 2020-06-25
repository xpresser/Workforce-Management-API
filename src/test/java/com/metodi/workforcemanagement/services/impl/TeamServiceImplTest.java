package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.configuration.ModelMapperConfig;
import com.metodi.workforcemanagement.controllers.dtos.team.TeamRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.team.TeamResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.entities.Team;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.TeamRepository;
import com.metodi.workforcemanagement.services.TeamService;
import com.metodi.workforcemanagement.services.UserService;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TeamServiceImplTest {

    private User loggedUser;
    private Team expectedTeam;
    private TeamResponseDTO teamResponseDTO;
    private final long testTeamId = 1;
    private final long teamMemberId = 1;
    private final Long adminUserId = 100L;

    @MockBean
    private TeamRepository teamRepository;

    @MockBean
    private UserService userService;

    @Autowired
    TeamService teamService;

    @Autowired
    ModelMapperConfig modelMapperConfig;

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        loggedUser = createAdminUser();
        expectedTeam = createTestTeam();
        modelMapper = modelMapperConfig.getModelMapper();
    }

    @Test
    @DisplayName("Should return a list of all existing teams.")
    void getAll() {
        List<Team> allTeams = new ArrayList<>();
        allTeams.add(expectedTeam);

        when(teamRepository.findAll())
                .thenReturn(allTeams);

        List<TeamResponseDTO> expectedTeamList = allTeams.stream()
                .map(team -> modelMapper.map(team, TeamResponseDTO.class))
                .collect(Collectors.toList());

        List<TeamResponseDTO> actualTeamList = teamService.getAll();

        assertAll(
                () -> assertNotNull(actualTeamList),
                () -> assertEquals(expectedTeamList, actualTeamList)
        );
        verify(teamRepository)
                .findAll();
    }

    @Test
    @DisplayName("Should return a team with the given ID.")
    void getTeamById() {
        when(teamRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.ofNullable(expectedTeam));

        teamResponseDTO = modelMapper.map(expectedTeam, TeamResponseDTO.class);

        TeamResponseDTO actualTeam = teamService.getTeamById(testTeamId);

        assertAll(
                () -> assertNotNull(actualTeam),
                () -> assertEquals(teamResponseDTO, actualTeam)
        );
        verify(teamRepository)
                .findById(any(Long.class));
    }

    @Test
    @DisplayName("Should edit team successfully and return it.")
    void editTeam() {
        TeamRequestDTO teamRequestDTO = createTeamRequestDTO();
        teamRequestDTO.setTitle("editedTestTeam");

        expectedTeam.setTitle("editedTestTeam");
        expectedTeam.setUpdatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        when(teamRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(expectedTeam));
        when(userService.getById(anyLong()))
                .thenReturn(loggedUser);
        when(teamRepository.saveAndFlush(any(Team.class)))
                .thenReturn(expectedTeam);

        teamResponseDTO = fromTeam(expectedTeam);

        TeamResponseDTO actualTeam = teamService.edit(testTeamId, teamRequestDTO);

        assertAll(
                () -> assertNotNull(actualTeam),
                () -> assertEquals(teamResponseDTO.getId(), actualTeam.getId())
        );

        verify(teamRepository)
                .findById(any(Long.class));

        verify(userService)
                .getById(anyLong());

        verify(teamRepository)
                .saveAndFlush(any(Team.class));
    }

    @Test
    @DisplayName("Should create team successfully")
    void create() {
        TeamRequestDTO teamRequestDTO = createTeamRequestDTO();
        teamResponseDTO = modelMapper.map(expectedTeam, TeamResponseDTO.class);

        when(teamRepository.save(any(Team.class)))
                .thenReturn(expectedTeam);
        when(userService.getById(anyLong()))
                .thenReturn(loggedUser);

        TeamResponseDTO actualTeam = teamService.create(teamRequestDTO);

        assertAll(
                () -> assertNotNull(actualTeam),
                () -> assertEquals(teamResponseDTO.getId(), actualTeam.getId())
        );

        verify(teamRepository)
                .save(any(Team.class));
        verify(userService)
                .getById(any());
    }

    @Test
    @DisplayName("Should delete team successfully")
    void deleteById() {
        when(teamRepository.removeById(anyLong()))
                .thenReturn(1);
        teamService.deleteById(testTeamId);

        verify(teamRepository)
                .removeById(any(Long.class));
    }

    @Test
    @DisplayName("Should assign new team member.")
    void assignMember() {
        when(teamRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.ofNullable(expectedTeam));

        when(userService.getById(anyLong()))
                .thenReturn(loggedUser);

        when(teamRepository.save(any(Team.class)))
                .thenReturn(expectedTeam);

        List<UserShortDTO> actualMembers = teamService.assignMember(teamMemberId, testTeamId);

        expectedTeam.getMembers().add(loggedUser);
        List<UserShortDTO> expectedMembers = expectedTeam.getMembers().stream()
                .map(member -> modelMapper.map(member, UserShortDTO.class))
                .collect(Collectors.toList());

        assertAll(
                () -> assertNotNull(actualMembers),
                () -> assertEquals(expectedMembers, actualMembers)
        );

        verify(teamRepository, times(2))
                .findById(any(Long.class));

        verify(userService)
                .getById(anyLong());

        verify(teamRepository)
                .save(any(Team.class));
    }

    @Test
    @DisplayName("Should unassign the given member and return a list of the members left.")
    void unassignMember() {
        when(teamRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.ofNullable(expectedTeam));

        when(userService.getById(anyLong()))
                .thenReturn(loggedUser);

        when(teamRepository.save(any(Team.class)))
                .thenReturn(expectedTeam);

        List<UserShortDTO> actualMembers = teamService.unassignMember(teamMemberId, testTeamId);
        List<UserShortDTO> expectedMembers = new ArrayList<>();

        assertAll(
                () -> assertNotNull(actualMembers),
                () -> assertEquals(expectedMembers, actualMembers)
        );

        verify(teamRepository, times(2))
                .findById(any(Long.class));

        verify(userService)
                .getById(anyLong());

        verify(teamRepository)
                .save(any(Team.class));
    }

    private User createAdminUser() {
        User adminUser = new User();
        Map<LeaveType, Integer> daysOff = new HashMap<>();
        adminUser.setUsername("admin");
        adminUser.setEmail("workforce.admin@gmail.com");
        adminUser.setPassword("adminpass");
        adminUser.setFirstName("Administrator");
        adminUser.setLastName("Administrator");
        adminUser.setAdmin(true);
        adminUser.setOnLeave(false);
        adminUser.setId(adminUserId);
        adminUser.setRemainingDaysOff(daysOff);
        adminUser.setCreatedBy(1L);
        adminUser.setCreatedAt(Instant.parse("2020-04-23T20:20:20.00Z"));
        adminUser.setUpdatedBy(1L);
        adminUser.setUpdatedAt(Instant.parse("2020-04-23T20:20:20.00Z"));
        return adminUser;
    }

    private Team createTestTeam() {
        long teamId = 1;
        Set<User> members = new HashSet<>();
        Team testTeam = new Team();
        testTeam.setTitle("testTeam");
        testTeam.setDescription("some description");
        testTeam.setTeamLeader(loggedUser);
        testTeam.setId(teamId);
        testTeam.setCreatedBy(loggedUser.getId());
        testTeam.setMembers(members);
        testTeam.setCreatedAt(Instant.parse("2020-04-23T20:20:20.00Z"));
        testTeam.setUpdatedBy(loggedUser.getId());
        testTeam.setUpdatedAt(Instant.parse("2020-04-23T20:20:20.00Z"));
        return testTeam;
    }

    private TeamResponseDTO fromTeam (Team team) {
        TeamResponseDTO teamResponseDTO = new TeamResponseDTO();
        teamResponseDTO.setId(team.getId());
        teamResponseDTO.setTitle(team.getTitle());
        teamResponseDTO.setDescription(team.getDescription());
        teamResponseDTO.setTeamLeader(modelMapper.map(team.getTeamLeader(), UserShortDTO.class));
        teamResponseDTO.setCreatedAt(team.getCreatedAt());
        teamResponseDTO.setUpdatedAt(team.getUpdatedAt());
        teamResponseDTO.setTeamMembers(new ArrayList<>());

        return teamResponseDTO;
    }

    private TeamRequestDTO createTeamRequestDTO() {
        TeamRequestDTO teamRequestDTO = new TeamRequestDTO();
        teamRequestDTO.setTitle("test team");
        teamRequestDTO.setDescription("some description");
        teamRequestDTO.setTeamLeaderId(adminUserId);
        return teamRequestDTO;
    }
}
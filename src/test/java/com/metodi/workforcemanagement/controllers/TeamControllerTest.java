package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.team.TeamResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.repositories.TeamRepository;
import com.metodi.workforcemanagement.services.TeamService;
import com.metodi.workforcemanagement.services.impl.JWTTokenServiceImpl;
import com.metodi.workforcemanagement.utils.TeamLeaderCheck;
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
import java.util.List;

import static com.metodi.workforcemanagement.test_resources.TeamJSON.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@WithMockUser(roles = "ADMIN")
class TeamControllerTest {

    private TeamResponseDTO testTeam;
    private String token;
    private final long teamId = 1;
    private final long memberId = 1;
    private final UserShortDTO adminUser = new UserShortDTO();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private JWTTokenServiceImpl tokenService;

    @MockBean
    private TeamService teamService;

    @MockBean
    TeamRepository teamRepository;

    @MockBean
    TeamLeaderCheck teamLeaderCheck;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        adminUser.setId(100L);
        adminUser.setUsername("Administrator");

        token = "some string";
        List<UserShortDTO> teamMembers = new ArrayList<>();
        testTeam = new TeamResponseDTO();
        testTeam.setTitle("testTeam");
        testTeam.setTeamLeader(adminUser);
        testTeam.setDescription("some description");
        testTeam.setId(teamId);
        testTeam.setCreatedBy(adminUser);
        testTeam.setCreatedAt(Instant.parse("2020-05-23T20:20:20.00Z"));
        testTeam.setUpdatedBy(adminUser);
        testTeam.setUpdatedAt(Instant.parse("2020-05-23T20:20:20.00Z"));
        testTeam.setTeamMembers(teamMembers);
    }

    @DisplayName("Get All should return json list of all existing teams")
    @Test
    void getAll() throws Exception {
        List<TeamResponseDTO> teams = new ArrayList<>();
        teams.add(testTeam);
        Pageable pageable = PageRequest.of(0, 10);
        Page<TeamResponseDTO> page = new PageImpl<>(teams, pageable, teams.size());

        when(teamService.getAll(any()))
                .thenReturn(page);

        mockMvc.perform(get("/teams")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(TEAM_GET_ALL_JSON));

        verify(teamService)
                .getAll(any());
    }

    @DisplayName("Get by ID should return the team with the specified ID in json format")
    @Test
    void getById() throws Exception {

        when(teamService.getTeamById(teamId))
                .thenReturn(testTeam);

        mockMvc.perform(get("/teams/{teamId}", teamId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(TEAM_GET_BY_ID_JSON));

        verify(teamService)
                .getTeamById(teamId);
    }

    @DisplayName("Add should return the team just created in json format.")
    @Test
    void create() throws Exception {
        when(teamService.create(any()))
                .thenReturn(testTeam);
        when(teamRepository.existsByTitle(anyString()))
                .thenReturn(false);

        mockMvc.perform( MockMvcRequestBuilders
                .post("/teams")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(    "{\"title\": \"testTeam\",\n" +
                        "\"description\": \"some description\",\n" +
                        "\"teamLeaderId\": 100}"
                )
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(TEAM_CREATE_JSON));

        verify(teamService)
                .create(any());
    }

    @DisplayName("Edit should return the team after edit in json format.")
    @Test
    void edit() throws Exception {
        testTeam.setTitle("editedTestTeam");

        when(teamService.edit( anyLong(), any()))
                .thenReturn(testTeam);

        mockMvc.perform( MockMvcRequestBuilders
                .put("/teams/{id}", teamId)
                .header(" Authorization", token)
                .content("{\"title\": \"editedTestTeam\",\n" +
                        "\"description\": \"some description\",\n" +
                        "\"teamLeaderId\": 100}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(TEAM_EDIT_JSON));

        verify(teamService)
                .edit(anyLong(), any());
    }

    @DisplayName("AssignMember should return json list of all team members with the new member added to it.")
    @Test
    void assignMember() throws Exception {

        List<UserShortDTO> members = new ArrayList<>();
        UserShortDTO newMember = new UserShortDTO();
        newMember.setId(100L);
        newMember.setUsername("testUser");
        members.add(newMember);

        when(teamService.assignMember( memberId, teamId))
                .thenReturn(members);

        mockMvc.perform(put("/teams/{teamId}/users/{userId}", teamId, memberId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\n" +
                        "           \"id\": 100,\n" +
                        "            \"username\": \"testUser\"\n" +
                        "         }]"));

        verify(teamService)
                .assignMember(memberId, teamId);
    }

    @DisplayName("unassignMember should return status Ok if team member is successfully unassigned, and the list of assigned members.")
    @Test
    void unassignMember() throws Exception {
        List<UserShortDTO> members = new ArrayList<>();

        when(teamService.unassignMember( memberId, teamId))
                .thenReturn(members);
        when(teamLeaderCheck.isTeamLead(anyLong(), anyLong()))
                .thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/teams/{teamId}/users/{userId}", teamId, memberId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        verify(teamService)
                .unassignMember(memberId, teamId);
    }
}
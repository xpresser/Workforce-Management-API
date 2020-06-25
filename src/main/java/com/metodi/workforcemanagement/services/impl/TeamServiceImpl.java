package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.configuration.ModelMapperConfig;
import com.metodi.workforcemanagement.controllers.dtos.team.TeamRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.team.TeamResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.entities.Team;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.TeamRepository;
import com.metodi.workforcemanagement.services.TeamService;
import com.metodi.workforcemanagement.services.UserService;
import com.metodi.workforcemanagement.services.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {
    private static final String TEAM_DOES_NOT_EXIST = "Team does not exist.";
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, UserService userService,
                           ModelMapperConfig modelMapper) {
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.modelMapper = modelMapper.getModelMapper();
    }

    @Override
    public List<TeamResponseDTO> getAll() {
        return teamRepository.findAll().stream()
                .map(team -> modelMapper.map(team, TeamResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TeamResponseDTO getTeamById(long teamId) {
        return modelMapper.map(getById(teamId), TeamResponseDTO.class);
    }

    @Override
    public Team getById(long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(TEAM_DOES_NOT_EXIST));
    }

    @Override
    public TeamResponseDTO edit(long teamId, TeamRequestDTO teamRequestDTO) {
        Team editedTeam = getById(teamId);
        User teamLead = userService.getById(teamRequestDTO.getTeamLeaderId());
        editedTeam.setTitle(teamRequestDTO.getTitle());
        editedTeam.setDescription(teamRequestDTO.getDescription());
        editedTeam.setTeamLeader(teamLead);
        editedTeam.getMembers().add(teamLead);
        
        return modelMapper.map(teamRepository.saveAndFlush(editedTeam), TeamResponseDTO.class);
    }

    @Override
    public TeamResponseDTO create(TeamRequestDTO teamRequestDTO) {
        Set<User> members = new HashSet<>();
        Team team = modelMapper.map(teamRequestDTO, Team.class);
        team.setTeamLeader(userService.getById(teamRequestDTO.getTeamLeaderId()));
        members.add(team.getTeamLeader());
        team.setMembers(members);

        return modelMapper.map(teamRepository.save(team), TeamResponseDTO.class);
    }

    @Override
    public Integer deleteById(long teamId) {
        return teamRepository.removeById(teamId);
    }

    @Override
    public List<UserShortDTO> assignMember(long memberId, long teamId) {
        Team team = getById(teamId);
        team.getMembers().add(userService.getById(memberId));

        teamRepository.save(team);
        return getMembers(teamId);
    }

    @Override
    public List<UserShortDTO> unassignMember(long memberId, long teamId) {
        Team team = getById(teamId);
        team.getMembers().remove(userService.getById(memberId));
        teamRepository.save(team);
        return getMembers(teamId);
    }

    @Override
    public Page<TeamResponseDTO> getAll(Pageable pageable) {
        Page<Team> teamsFromDatabase = this.teamRepository.findAll(pageable);
        long totalElements = teamsFromDatabase.getTotalElements();
        return new PageImpl<>(
                teamsFromDatabase.stream()
                        .map(u -> this.modelMapper.map(u, TeamResponseDTO.class))
                        .collect(Collectors.toList()), pageable, totalElements);
    }

    private List<UserShortDTO> getMembers(long teamId) {
        return getById(teamId).getMembers().stream()
                .map(member -> modelMapper.map(member, UserShortDTO.class))
                .collect(Collectors.toList());
    }
}

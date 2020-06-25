package com.metodi.workforcemanagement.utils;

import com.metodi.workforcemanagement.entities.Team;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.TeamRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.TimeOffRequestService;
import com.metodi.workforcemanagement.services.UserService;
import com.metodi.workforcemanagement.services.exceptions.ResourceNotFoundException;
import com.metodi.workforcemanagement.services.exceptions.UnauthorizedUserException;
import com.metodi.workforcemanagement.services.exceptions.UserTeamLeaderRoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class TeamLeaderCheck {
    private final AuthenticationService authenticationService;
    private final TeamRepository teamRepository;
    private final TimeOffRequestService timeOffRequestService;
    private final UserService userService;

    @Autowired
    public TeamLeaderCheck(AuthenticationService authenticationService,
                           TeamRepository teamRepository,
                           @Lazy TimeOffRequestService timeOffRequestService,
                           UserService userService) {
        this.authenticationService = authenticationService;
        this.teamRepository = teamRepository;
        this.timeOffRequestService = timeOffRequestService;
        this.userService = userService;
    }

    public boolean loggedUserIsTeamLeadOrAdmin() {
        User loggedUser = authenticationService.getLoggedUser();
        if (teamRepository.existsByTeamLeaderId(loggedUser.getId()) || loggedUser.isAdmin()) {
            return true;
        } else {
            throw new UnauthorizedUserException("The user is not team leader or Admin");
        }
    }

    public boolean loggedUserIsTeamLead() {

        User loggedUser = authenticationService.getLoggedUser();
        if (teamRepository.existsByTeamLeaderId(loggedUser.getId())) {
            return true;
        } else {
            throw new UnauthorizedUserException("The user is not team leader");
        }
    }

    public boolean isTeamLead(Long userId, Long teamId) {
        Team editedTeam = teamRepository.findById(teamId)
                .orElseThrow(() ->new ResourceNotFoundException("No team exists with the given ID"));
        boolean isTeamLeader = editedTeam.getTeamLeader().getId().equals(userId);
        if (!isTeamLeader) {
            return false;
        } else {
            throw new UserTeamLeaderRoleException();
        }
    }

    public boolean isApprover(Long timeOffRequestId) {
        User loggedUser = authenticationService.getLoggedUser();
        TimeOffRequest timeOffRequest = timeOffRequestService.getById(timeOffRequestId);
        User requester = userService.getById(timeOffRequest.getRequesterId());
        boolean isApprover = requester.getTeams().stream()
                .anyMatch(team -> team.getTeamLeader().equals(loggedUser));

        if(isApprover) {
            return true;
        } else {
            throw new UnauthorizedUserException("The user is not approver for that request");
        }

    }
}

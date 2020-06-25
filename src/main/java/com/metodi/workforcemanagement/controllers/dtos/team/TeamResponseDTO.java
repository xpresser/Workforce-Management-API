package com.metodi.workforcemanagement.controllers.dtos.team;

import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter @Setter @EqualsAndHashCode
public class TeamResponseDTO {

    private Long id;

    private UserShortDTO teamLeader;

    private String title;

    private String description;

    private UserShortDTO createdBy;

    private UserShortDTO updatedBy;

    private Instant createdAt;

    private Instant updatedAt;

    private List<UserShortDTO> teamMembers;

    @Override
    public String toString() {
        return "TeamResponseDTO{" +
                "id=" + id +
                ", teamLeaderId=" + teamLeader +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", memberIds=" + teamMembers +
                '}';
    }
}

package com.metodi.workforcemanagement.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "TEAMS")
@Getter @Setter
public class Team extends Auditable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "TEAM_LEADER",referencedColumnName = "ID")
    private User teamLeader;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany(cascade =
            {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "TEAM_MEMBERS",
            joinColumns = {
                    @JoinColumn(
                            name = "TEAM_ID",
                            referencedColumnName = "ID"
                    )
            },
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "USER_ID",
                            referencedColumnName = "ID"
                    )
            }
    )
    private Set<User> members = new HashSet<>();

    @PreRemove
    private void removeTeamFromUsersProfile() {
        for (User m : members) {
            m.getTeams().remove(this);
        }
    }
}

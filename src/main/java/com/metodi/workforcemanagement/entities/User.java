package com.metodi.workforcemanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User extends Auditable<Long> {

    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "days_off_allowances",
            joinColumns = {@JoinColumn(name = "user_id",referencedColumnName = "id")})
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "leave_days_left")
    private Map<LeaveType, Integer> remainingDaysOff;

    private String username;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "on_leave")
    private boolean onLeave;

    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonBackReference
    private Set<Team> teams = new HashSet<>();
}

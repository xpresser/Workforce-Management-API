package com.metodi.workforcemanagement.entities;

import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.controllers.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "time_off_requests")
@Getter @Setter @NoArgsConstructor
public class TimeOffRequest extends Auditable<Long> {

    @Column(name = "LEAVE_TYPE")
    @MapKeyEnumerated(EnumType.STRING)
    private LeaveType leaveType;

    @MapKeyEnumerated(EnumType.STRING)
    private Status status;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "requester_id")
    private Long requesterId;

    private String reason;

    @Column(name = "LEAVE_WORK_DAYS")
    private Integer leaveWorkDays;

    @OneToMany(mappedBy = "request", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimeOffResponse> responses = new HashSet<>();
}

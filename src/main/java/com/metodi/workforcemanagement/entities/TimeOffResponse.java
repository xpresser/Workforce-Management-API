package com.metodi.workforcemanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "time_off_responses")
@Getter @Setter @NoArgsConstructor
public class TimeOffResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    @JsonBackReference
    private TimeOffRequest request;

    @ManyToOne
    @JoinColumn(name = "approver_id", referencedColumnName = "id")
    private User approver;

    @Column(name = "isApproved")
    private boolean isApproved;
}

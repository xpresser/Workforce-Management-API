package com.metodi.workforcemanagement.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "GDPR_ARCHIVE")
@Getter @Setter @NoArgsConstructor
public class LeftUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String username;

    @Column(name = "deletion_date")
    private Instant deletionDate;
}

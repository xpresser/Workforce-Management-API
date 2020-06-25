package com.metodi.workforcemanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class Auditable<U>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreatedBy
    @Column(name = "created_by")
    @JsonBackReference
    private U createdBy;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    @JsonBackReference
    private U updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
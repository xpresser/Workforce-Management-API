package com.metodi.workforcemanagement.repositories;

import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TimeOffRequestRepository extends JpaRepository<TimeOffRequest, Long> {
    
    int removeById(Long requestId);

    @Query("SELECT COUNT(rq.id) from TimeOffRequest rq " +
            "where rq.createdBy = :userId")
    int isRequestCreator(Long userId);

    @Query("SELECT rq from TimeOffRequest rq " +
            "where rq.createdBy = :userId")
    List<TimeOffRequest> findTimeOffRequestByCreatorBy(Long userId);

    List<TimeOffRequest> findAllByRequesterId(Long userId);

    List<TimeOffRequest> findAllByStatusEqualsAndEndDate(Status status, LocalDate endDate);

    List<TimeOffRequest> findAllByStatusEqualsAndStartDate(Status status, LocalDate startDate);

    Boolean existsByRequesterIdAndStatus(Long requesterId, Status status);

    Page<TimeOffRequest> findAllByRequesterId(Pageable pageable, Long requesterId);
}

package com.metodi.workforcemanagement.repositories;

import com.metodi.workforcemanagement.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Integer removeById(Long id);
    boolean existsByTeamLeaderId(Long teamLeader_id);
    Boolean existsByTitle(String title);

    @Query("select t from Team t " +
            "where t.createdBy = :userId")
    List<Team> findTeamsByCreatorBy(Long userId);

    @Query("SELECT COUNT(t.id) from Team t " +
            "where t.createdBy = :userId")
    int isTeamCreator(Long userId);
}

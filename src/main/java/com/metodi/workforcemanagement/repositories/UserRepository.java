package com.metodi.workforcemanagement.repositories;

import com.metodi.workforcemanagement.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByEmailAndId(String email, Long userId);
    Boolean existsByUsername(String username);
    Boolean existsByUsernameAndId(String username, long userId);
    Integer removeById(Long id);
    Page<User> findAll(Pageable pageable);

    @Query("SELECT DISTINCT(u.email) from User u join u.teams t where t.id in " +
            "(select tm.id from User us join us.teams tm where us.id = :userId)")
    String[] findAllTeammatesEmails(Long userId);

    @Query("SELECT DISTINCT(u.email) from User u join u.teams t where u.id in " +
            "(select tm.teamLeader" +
            " from User us join us.teams tm where us.id = :userId)")
    String[] findAllTeamLeaderEmailsForCurrentUser(Long userId);

    @Query("SELECT COUNT(t.id) from User u join u.teams t " +
            "where t.teamLeader = u and u.id = :userId")
    int userIsTeamLeader(Long userId);

    @Query("select t.id from User u join u.teams t " +
            "where t.teamLeader = u and u.id = :userId")
    List<Long> findTeamsWhereHaveTeamLeaderRole(Long userId);

    @Query("select u from User u " +
            "where u.createdBy = :userId")
    List<User> findUsersByCreatorBy(Long userId);

    @Query("SELECT COUNT(u.id) from User u " +
            "where u.createdBy = :userId")
    int userIsUsersCreator(Long userId);
}

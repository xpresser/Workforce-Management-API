package com.metodi.workforcemanagement.events.schedule;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.LeftUser;
import com.metodi.workforcemanagement.entities.Team;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.LeftUserRepository;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.TimeOffResponseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ScheduleTest {
    @MockBean
    private LeftUserRepository leftUserRepo;

    @MockBean
    UserRepository userRepository;

    @MockBean
    private TimeOffRequestRepository timeOffRequestRepo;

    @MockBean
    TimeOffResponseService timeOffResponseService;

    @Autowired
    Schedule schedule;

    @Test
    void deleteLeftUserDataThatPassedSixMonthsTestScheduler() {
        org.springframework.scheduling.support.CronTrigger trigger =
                new CronTrigger("0 0 1 * * *");
        Calendar today = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        final Date yesterday = today.getTime();

        Date nextExecutionTime = trigger.nextExecutionTime(
                new TriggerContext() {

                    @Override
                    public Date lastScheduledExecutionTime() {
                        return yesterday;
                    }

                    @Override
                    public Date lastActualExecutionTime() {
                        return yesterday;
                    }

                    @Override
                    public Date lastCompletionTime() {
                        return yesterday;
                    }
                });

        String expectedMessage = "Next Execution date: 01:00:00";
        String actualMessage = "Next Execution date: " + df.format(nextExecutionTime);

        assertAll(
                () -> assertEquals(expectedMessage, actualMessage)
        );
    }

    @Test
    void deleteLeftUserDataThatPassedSixMonthsTest() {
        LeftUser deletedUser;
        deletedUser = createDeletedUser();
        List<LeftUser> listOfDeletedUsers = new ArrayList<>();
        listOfDeletedUsers.add(deletedUser);

        when(leftUserRepo.findAll())
                .thenReturn(listOfDeletedUsers);
        when(timeOffRequestRepo.findAllByRequesterId(anyLong()))
                .thenReturn(getListOfRequestsForTheTestUser());

        schedule.deleteLeftUserDataThatPassedSixMonths();

        verify(leftUserRepo)
                .findAll();
        verify(timeOffRequestRepo)
                .findAllByRequesterId(anyLong());
        verify(timeOffRequestRepo, times(2))
                .delete(any());
        verify(leftUserRepo)
                .delete(any());
    }

    @Test
    void checkUsersLeaveTestScheduler() {
        org.springframework.scheduling.support.CronTrigger trigger =
                new CronTrigger("0 0 2 * * *");
        Calendar today = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        final Date yesterday = today.getTime();

        Date nextExecutionTime = trigger.nextExecutionTime(
                new TriggerContext() {

                    @Override
                    public Date lastScheduledExecutionTime() {
                        return yesterday;
                    }

                    @Override
                    public Date lastActualExecutionTime() {
                        return yesterday;
                    }

                    @Override
                    public Date lastCompletionTime() {
                        return yesterday;
                    }
                });

        String expectedMessage = "Next Execution date: 02:00:00";
        String actualMessage = "Next Execution date: " + df.format(nextExecutionTime);

        assertAll(
                () -> assertEquals(expectedMessage, actualMessage)
        );
    }

    @Test
    void checkUsersLeave() {
        TimeOffRequest timeOffRequest = new TimeOffRequest();
        timeOffRequest.setRequesterId(101L);
        User userReturningFromHoliday = new User();
        userReturningFromHoliday.setId(101L);
        userReturningFromHoliday.setOnLeave(true);
        List<TimeOffRequest> timeOffRequests = new ArrayList<>();
        timeOffRequests.add(timeOffRequest);

        when(timeOffRequestRepo.findAllByStatusEqualsAndEndDate(any(),any()))
                .thenReturn(timeOffRequests);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userReturningFromHoliday));
        when(timeOffRequestRepo.findAllByStatusEqualsAndStartDate(any(),any()))
                .thenReturn(timeOffRequests);

        schedule.checkUsersLeave();

        verify(timeOffRequestRepo)
                .findAllByStatusEqualsAndEndDate(any(),any());
        verify(userRepository, times(2))
                .findById(anyLong());
        verify(userRepository, times(2) )
                .save(any());
        verify(timeOffRequestRepo)
                .findAllByStatusEqualsAndStartDate(any(), any());
    }

    @Test
    void transferRemainingLeaveTestScheduler() {
        org.springframework.scheduling.support.CronTrigger trigger =
                new CronTrigger("0 0 3 1 1 *");
        Calendar today = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date yesterday = today.getTime();

        Date nextExecutionTime = trigger.nextExecutionTime(
                new TriggerContext() {

                    @Override
                    public Date lastScheduledExecutionTime() {
                        return yesterday;
                    }

                    @Override
                    public Date lastActualExecutionTime() {
                        return yesterday;
                    }

                    @Override
                    public Date lastCompletionTime() {
                        return yesterday;
                    }
                });

        String expectedMessage = String.format("Next Execution date: %s-01-01 03:00:00", Calendar.getInstance().get(Calendar.YEAR)+1);
        String actualMessage = "Next Execution date: " + df.format(nextExecutionTime);

        assertAll(
                () -> assertEquals(expectedMessage, actualMessage)
        );
    }

    @Test
    void transferRemainingLeave() {
        Map<LeaveType, Integer> remainingDaysOff = new HashMap<>();
        remainingDaysOff.put(LeaveType.PAID_LEAVE, 10);
        remainingDaysOff.put(LeaveType.SICK_LEAVE, 20);
        remainingDaysOff.put(LeaveType.UNPAID_LEAVE, 20);
        User user = new User();
        user.setId(101L);
        user.setRemainingDaysOff(remainingDaysOff);
        List<User> users = new ArrayList<>();
        users.add(user);

        when( userRepository.findAll())
                .thenReturn(users);

        schedule.transferRemainingLeave();

        verify(userRepository )
                .findAll();
        verify(userRepository)
                .save(any());
    }

    @Test
    void autoApproveAwaitingRequestsTestScheduler() {
        org.springframework.scheduling.support.CronTrigger trigger =
                new CronTrigger("0 0 6 * * * ");
        Calendar today = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        final Date yesterday = today.getTime();

        Date nextExecutionTime = trigger.nextExecutionTime(
                new TriggerContext() {

                    @Override
                    public Date lastScheduledExecutionTime() {
                        return yesterday;
                    }

                    @Override
                    public Date lastActualExecutionTime() {
                        return yesterday;
                    }

                    @Override
                    public Date lastCompletionTime() {
                        return yesterday;
                    }
                });

        String expectedMessage = "Next Execution date: 06:00:00";
        String actualMessage = "Next Execution date: " + df.format(nextExecutionTime);

        assertAll(
                () -> assertEquals(expectedMessage, actualMessage)
        );
    }

    @Test
    void autoApproveAwaitingRequests() {
        User approver = new User();
        User requester = new User();
        Team team = new Team();
        team.setId(100L);
        team.getMembers().add(approver);
        team.setTeamLeader(approver);
        requester.setId(101L);
        requester.setOnLeave(true);
        requester.getTeams().add(team);
        team.getMembers().add(requester);
        approver.setOnLeave(true);

        TimeOffRequest timeOffRequest = new TimeOffRequest();
        timeOffRequest.setId(100L);
        timeOffRequest.setRequesterId(101L);
        List<TimeOffRequest> timeOffRequests = new ArrayList<>();
        timeOffRequests.add(timeOffRequest);

        when( timeOffRequestRepo.findAllByStatusEqualsAndStartDate(any(), any()))
                .thenReturn(timeOffRequests);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester));

        schedule.autoApproveAwaitingRequests();

        verify(timeOffRequestRepo )
                .findAllByStatusEqualsAndStartDate(any(), any());
        verify(userRepository)
                .findById(anyLong());
        verify(timeOffResponseService)
                .create(anyLong(), any());
    }

    private LeftUser createDeletedUser() {
        LeftUser deletedUser = new LeftUser();
        deletedUser.setUserId(150L);
        deletedUser.setUsername("I was fired!");
        deletedUser.setDeletionDate(Instant.now().atOffset(ZoneOffset.UTC).minus(6, ChronoUnit.MONTHS).toInstant()
                .minus(1, ChronoUnit.DAYS));

        return deletedUser;
    }

    private List<TimeOffRequest> getListOfRequestsForTheTestUser() {
        List<TimeOffRequest> timeOffRequestForCurrentUser = new ArrayList<>();
        TimeOffRequest timeOffRequest = new TimeOffRequest();
        timeOffRequest.setId(100L);
        timeOffRequest.setRequesterId(150L);
        timeOffRequest.setStatus(Status.APPROVED);
        timeOffRequest.setLeaveType(LeaveType.PAID_LEAVE);
        timeOffRequest.setStartDate(LocalDate.parse("2020-07-06"));
        timeOffRequest.setEndDate(LocalDate.parse("2020-07-17"));
        timeOffRequest.setLeaveWorkDays(10);
        timeOffRequest.setReason("Holiday");

        TimeOffRequest timeOffRequest2 = new TimeOffRequest();
        timeOffRequest2.setId(100L);
        timeOffRequest2.setRequesterId(150L);
        timeOffRequest2.setStatus(Status.APPROVED);
        timeOffRequest2.setLeaveType(LeaveType.PAID_LEAVE);
        timeOffRequest2.setStartDate(LocalDate.parse("2020-08-06"));
        timeOffRequest2.setEndDate(LocalDate.parse("2020-08-17"));
        timeOffRequest2.setLeaveWorkDays(10);
        timeOffRequest2.setReason("Holiday");

        timeOffRequestForCurrentUser.add(timeOffRequest);
        timeOffRequestForCurrentUser.add(timeOffRequest2);

        return timeOffRequestForCurrentUser;
    }
}

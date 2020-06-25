package com.metodi.workforcemanagement.events.listeners;

import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.events.time_off_request_events.RequestCreatedEvent;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.CalendarService;
import com.metodi.workforcemanagement.services.EmailService;
import com.metodi.workforcemanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@SuppressWarnings("ALL")
@Component
public class RequestCreatedListener {

    private final EmailService emailService;

    private final CalendarService calendarService;

    private final UserService userService;

    private final UserRepository userRepository;

    @Value("${email.message.sick_leave.to.members}")
    String messageToTeammates;

    @Value("${email.message.submitted.to.team_leaders}")
    String messageToTeamLeaders;

    @Value("${email.message.approved.to.requester}")
    String messageApprovedToRequester;

    @Value("${email.message.submitted.to.requester}")
    String messageSubmittedToRequester;

    @Autowired
    public RequestCreatedListener(EmailService emailService,
                                  CalendarService calendarService,
                                  UserService userService,
                                  UserRepository userRepository) {
        this.emailService = emailService;
        this.calendarService = calendarService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @EventListener(condition = "#event.getTimeOffRequest().getStatus().toString() eq 'APPROVED'")
    public void handleRequestSickLeave(RequestCreatedEvent event) {
        TimeOffRequest timeOffRequest = event.getTimeOffRequest();
        User requester = userService.getById(timeOffRequest.getRequesterId());
        int desiredDaysOff = timeOffRequest.getLeaveWorkDays();

        deductApprovedLeaveDays(requester, desiredDaysOff);

        LeaveType leaveType = timeOffRequest.getLeaveType();

        if (leaveType.equals(LeaveType.SICK_LEAVE)) {
            sendEmailToTeammates(requester);
        }

        sendEmalToRequester(leaveType, requester, messageApprovedToRequester);
    }

    @EventListener(condition = "#event.getTimeOffRequest().getStatus().toString() eq 'AWAITING'")
    public void handleRequestPaindOrUnpaidLeave(RequestCreatedEvent event) {
        TimeOffRequest timeOffRequest = event.getTimeOffRequest();

        LeaveType leaveType = timeOffRequest.getLeaveType();
        User requester = userService.getById(timeOffRequest.getRequesterId());

        sendEmailToTeamLeaders(leaveType, requester);

        sendEmalToRequester(leaveType, requester, messageSubmittedToRequester);
    }

    private void sendEmailToTeamLeaders(LeaveType leaveType, User requester) {
        emailService.sendEmails(userRepository.findAllTeamLeaderEmailsForCurrentUser(requester.getId()),
                requester.getUsername(), String.format(messageToTeamLeaders, requester.getFirstName(), leaveType));
    }

    private void sendEmailToTeammates(User requester) {
        emailService.sendEmails(userRepository.findAllTeammatesEmails(requester.getId()),
                requester.getUsername(), String.format(messageToTeammates, requester.getFirstName()));
    }

    private void sendEmalToRequester(LeaveType leaveType, User requester, String message) {
        emailService.sendEmails(new String[]{requester.getEmail()},
                requester.getUsername(), String.format(message, leaveType));
    }

    private int setLeaveWorkDays(TimeOffRequest editedTimeOffRequest) {
        LocalDate startDate = editedTimeOffRequest.getStartDate();
        LocalDate endDate = editedTimeOffRequest.getEndDate();

        return this.calendarService.getNumberOfWorkingDays(startDate, endDate);
    }

    private void deductApprovedLeaveDays(User requester, int approvedLeaveDays) {
        requester.getRemainingDaysOff().compute(LeaveType.SICK_LEAVE, (k, v) -> v -= approvedLeaveDays);
    }
}

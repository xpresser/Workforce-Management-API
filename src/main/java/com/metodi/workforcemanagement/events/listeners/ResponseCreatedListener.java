package com.metodi.workforcemanagement.events.listeners;

import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.Team;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.events.time_off_response_events.ApprovedRequestEvent;
import com.metodi.workforcemanagement.events.time_off_response_events.ResponseCreatedEvent;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.EmailService;
import com.metodi.workforcemanagement.services.exceptions.NotFoundUserByIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:email-values.properties")
public class ResponseCreatedListener {
    private final EmailService emailService;
    private final UserRepository userRepository;
    @Value("${email.message.approved.request}")
    private String requestApprovedMessage;
    @Value("${email.message.rejected.request}")
    private String requestRejectedMessage;
    @Value("${email.template.notify.requester}")
    private String notifyRequesterMessage;

    @Autowired
    public ResponseCreatedListener(EmailService emailService,
                                   UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @EventListener(condition = "#event.getTimeOffResponse().approved eq true ")
    public ApprovedRequestEvent handleResponseApproved(ResponseCreatedEvent event) {
        TimeOffRequest timeOffRequest = event.getTimeOffResponse().getRequest();
        User requester = userRepository.findById(timeOffRequest.getRequesterId())
                .orElseThrow(() -> new NotFoundUserByIdException(timeOffRequest.getRequesterId()));
        timeOffRequest.getResponses().add(event.getTimeOffResponse());

        Set<User> requesterDistinctTeamLeaders = requester.getTeams()
                .stream()
                .map(Team::getTeamLeader).collect(Collectors.toSet());

        int approversCount = requesterDistinctTeamLeaders.size();
        int responsesCount = timeOffRequest.getResponses().size();
        if ( responsesCount == approversCount) {
            timeOffRequest.setStatus(Status.APPROVED);
        }
        return new ApprovedRequestEvent(timeOffRequest);
    }

    @EventListener(condition = "#event.timeOffRequest.status.toString() eq 'APPROVED'")
    public void handleApprovedRequestEvent(ApprovedRequestEvent event) {
        TimeOffRequest timeOffRequest = event.getTimeOffRequest();
        User requester = userRepository.findById(timeOffRequest.getRequesterId())
                .orElseThrow(() -> new NotFoundUserByIdException(timeOffRequest.getRequesterId()));

        requester.getRemainingDaysOff()
                .compute(timeOffRequest.getLeaveType(), (k,v) -> v -= timeOffRequest.getLeaveWorkDays());

        sendApprovedRequestNotifications(timeOffRequest, requester);
        notifyRequester(requester,timeOffRequest);
    }
    
    @EventListener(condition = "#event.getTimeOffResponse().approved eq false ")
    public void handleResponseRejected(ResponseCreatedEvent event) {
        TimeOffRequest timeOffRequest = event.getTimeOffResponse().getRequest();
        timeOffRequest.setStatus(Status.REJECTED);
        User requester = userRepository.findById(timeOffRequest.getRequesterId())
                .orElseThrow(() -> new NotFoundUserByIdException(timeOffRequest.getRequesterId()));

        sendRejectedRequestNotifications(timeOffRequest, requester, event.getTimeOffResponse().getApprover());
        notifyRequester(requester,timeOffRequest);
    }

    private void sendApprovedRequestNotifications(TimeOffRequest timeOffRequest, User requester) {
        emailService.sendEmails(userRepository.findAllTeamLeaderEmailsForCurrentUser(requester.getId()),
                requester.getFirstName(), String.format(requestApprovedMessage,
                        timeOffRequest.getLeaveType().toString(), requester.getUsername()));
    }

    private void sendRejectedRequestNotifications(TimeOffRequest timeOffRequest, User requester, User approver) {
        emailService.sendEmails(userRepository.findAllTeamLeaderEmailsForCurrentUser(requester.getId()),
                requester.getFirstName(), String.format(requestRejectedMessage,
                        approver.getUsername(), timeOffRequest.getLeaveType().toString(), requester.getUsername()));
    }

    private void notifyRequester(User requester, TimeOffRequest timeOffRequest) {
        String[] requesterEmailAddress = {requester.getEmail()};
        emailService.sendEmails(requesterEmailAddress,requester.getFirstName(),
                String.format(notifyRequesterMessage, timeOffRequest.getStatus().toString()));
    }
}

package com.metodi.workforcemanagement.events.schedule;

import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalRequestDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.*;
import com.metodi.workforcemanagement.repositories.LeftUserRepository;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.TimeOffResponseService;
import com.metodi.workforcemanagement.services.exceptions.NotFoundUserByIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Lazy(false)
@Component
public class Schedule {

    private LeftUserRepository leftUserRepo;
    private TimeOffRequestRepository timeOffRequestRepo;
    private UserRepository userRepository;
    private TimeOffResponseService timeOffResponseService;

    @Autowired
    public Schedule(LeftUserRepository leftUserRepo, TimeOffRequestRepository timeOffRequestRepo,
                    UserRepository userRepository, TimeOffResponseService timeOffResponseService) {
        this.leftUserRepo = leftUserRepo;
        this.timeOffRequestRepo = timeOffRequestRepo;
        this.userRepository = userRepository;
        this.timeOffResponseService = timeOffResponseService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void deleteLeftUserDataThatPassedSixMonths() {
        List<LeftUser> allLeftUsers = leftUserRepo.findAll();
        Instant currentDay = Instant.now();

        for (LeftUser currentLeftUser : allLeftUsers) {

            if (currentLeftUser.getDeletionDate().plus(Duration.ofDays(183)).isBefore(currentDay)) {
                List<TimeOffRequest> timeOffRequestForCurrentUser =
                        timeOffRequestRepo.findAllByRequesterId(currentLeftUser.getUserId());

                for (TimeOffRequest currentTimeOffRequest : timeOffRequestForCurrentUser) {
                    timeOffRequestRepo.delete(currentTimeOffRequest);
                }

                leftUserRepo.delete(currentLeftUser);
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void checkUsersLeave(){
         leaveUsersLeaveIsOver();

         leaveUsersLeaveIsStart();
    }

    @Scheduled(cron = "0 0 3 1 1 *")
    public void transferRemainingLeave() {

        List<User> userList = userRepository.findAll();
        for (User currentUser : userList) {

            remainingLeaveTransfer(currentUser);

            userRepository.save(currentUser);
        }
    }

    @Scheduled(cron = "0 0 6 * * * ")
    public void autoApproveAwaitingRequests() {
        List<TimeOffRequest> awaitingRequestsWhereLeaveStartsTomorrow =
                timeOffRequestRepo.findAllByStatusEqualsAndStartDate(Status.AWAITING, LocalDate.now().plusDays(1));
        for(TimeOffRequest request : awaitingRequestsWhereLeaveStartsTomorrow) {
            List<User> notAnsweredApprovers = getNotAnsweredApprovers(request);
            approveRequestIfApproverOnLeave(request, notAnsweredApprovers);
        }
    }

    private void approveRequestIfApproverOnLeave(TimeOffRequest request, List<User> notAnsweredApprovers) {
        ApprovalRequestDTO approvalRequestDTO = new ApprovalRequestDTO();
        approvalRequestDTO.setApproved(true);
        for ( User approver : notAnsweredApprovers ) {
            if (approver.isOnLeave()) {
                timeOffResponseService.create(request.getId(), approvalRequestDTO);
            }
        }
    }

    private List<User> getNotAnsweredApprovers(TimeOffRequest request) {
        User requester = userRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new NotFoundUserByIdException(request.getRequesterId()));
        return requester.getTeams().stream()
                .map(Team::getTeamLeader)
                .filter(approver -> !request.getResponses().stream().map(TimeOffResponse::getApprover).collect(Collectors.toList())
                        .contains(approver)).collect(Collectors.toList());
       }

    private void leaveUsersLeaveIsOver() {

        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepo
                .findAllByStatusEqualsAndEndDate(Status.APPROVED, LocalDate.now().minusDays(1));

        for (TimeOffRequest currentTimeOffRequest : timeOffRequestList) {
            User user = userRepository.findById(currentTimeOffRequest.getRequesterId())
                    .orElseThrow(() -> new NotFoundUserByIdException(currentTimeOffRequest.getRequesterId()));

            user.setOnLeave(false);

            this.userRepository.save(user);
        }
    }

    private void leaveUsersLeaveIsStart() {

        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepo
                .findAllByStatusEqualsAndStartDate(Status.APPROVED, LocalDate.now());

        for (TimeOffRequest currentTimeOffRequest : timeOffRequestList) {
            User user = userRepository.findById(currentTimeOffRequest.getRequesterId())
                    .orElseThrow(() -> new NotFoundUserByIdException(currentTimeOffRequest.getRequesterId()));

            user.setOnLeave(true);

            this.userRepository.save(user);
        }
    }

    private void remainingLeaveTransfer(User currentUser) {

        if(currentUser.getRemainingDaysOff().get(LeaveType.PAID_LEAVE) < 30) {
            currentUser.getRemainingDaysOff().compute(LeaveType.PAID_LEAVE,
                    (k, v) -> v += (30 - currentUser.getRemainingDaysOff().get(LeaveType.PAID_LEAVE)));
        }

        currentUser.getRemainingDaysOff().put(LeaveType.SICK_LEAVE, 40);
        currentUser.getRemainingDaysOff().put(LeaveType.UNPAID_LEAVE, 90);
    }
}

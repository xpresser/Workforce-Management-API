package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.configuration.ModelMapperConfig;
import com.metodi.workforcemanagement.controllers.dtos.user.UserRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserResponseDTO;
import com.metodi.workforcemanagement.controllers.enums.LeaveType;
import com.metodi.workforcemanagement.controllers.enums.Status;
import com.metodi.workforcemanagement.entities.LeftUser;
import com.metodi.workforcemanagement.entities.Team;
import com.metodi.workforcemanagement.entities.TimeOffRequest;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.LeftUserRepository;
import com.metodi.workforcemanagement.repositories.TeamRepository;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.UserService;
import com.metodi.workforcemanagement.services.exceptions.NotFoundUserByIdException;
import com.metodi.workforcemanagement.services.exceptions.UserTeamLeaderRoleException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepo;
    private final TeamRepository teamRepository;
    private final TimeOffRequestRepository timeOffRequestRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;
    private final LeftUserRepository leftUserRepo;

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder,
                           UserRepository userRepo,
                           TeamRepository teamRepository,
                           TimeOffRequestRepository timeOffRequestRepository,
                           ModelMapperConfig modelMapper,
                           AuthenticationService authenticationService,
                           LeftUserRepository leftUserRepo) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepo = userRepo;
        this.teamRepository = teamRepository;
        this.timeOffRequestRepository = timeOffRequestRepository;
        this.modelMapper = modelMapper.getModelMapper();
        this.authenticationService = authenticationService;
        this.leftUserRepo = leftUserRepo;
    }

    @Override
    public Page<UserResponseDTO> getAll(Pageable pageable) {
        Page<User> usersFromDatabase = this.userRepo.findAll(pageable);
        long totalElements = usersFromDatabase.getTotalElements();
        return new PageImpl<>(
                usersFromDatabase.stream()
                        .map(u -> this.modelMapper.map(u, UserResponseDTO.class))
                        .collect(Collectors.toList()), pageable, totalElements);

    }

    @Override
    public List<UserResponseDTO> getAll() {
        return this.userRepo.findAll()
                .stream()
                .map(u -> this.modelMapper.map(u, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(long userId) {
        User userById = this.getById(userId);
        return this.modelMapper.map(userById, UserResponseDTO.class);
    }

    @Override
    public User getById(Long id) {
        return this.userRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundUserByIdException(id));
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO userDTO) {
        User userToCreate = this.modelMapper.map(userDTO, User.class);

        userToCreate.setOnLeave(false);
        userToCreate.setRemainingDaysOff(daysOffAllowance());

        String encryptedPassword = this.bCryptPasswordEncoder.encode(userToCreate.getPassword());
        userToCreate.setPassword(encryptedPassword);

        User createdUser = this.userRepo.save(userToCreate);
        return this.modelMapper.map(createdUser, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO) {
        User editUser = this.getById(userId);
        editUser.setUsername(userRequestDTO.getUsername());
        editUser.setEmail(userRequestDTO.getEmail());
        editUser.setFirstName(userRequestDTO.getFirstName());
        editUser.setLastName(userRequestDTO.getLastName());
        editUser.setAdmin(userRequestDTO.isAdmin());
        editUser.setOnLeave(userRequestDTO.isOnLeave());
        editUser.setPassword(
                this.bCryptPasswordEncoder.encode(userRequestDTO.getPassword()));

        return this.modelMapper.map(
                this.userRepo.saveAndFlush(editUser)
                , UserResponseDTO.class);
    }

    @Override
    public Integer deleteById(Long userId) {
        if (isTeamLeader(userId)) {
            throw new UserTeamLeaderRoleException();
        }
        if (isUsersCreator(userId)) {
            changeUsersCreatorByField(userId);
        }
        if (isTeamsCreator(userId)) {
            changeTeamsCreatorByField(userId);
        }
        if (isRequestsCreator(userId)) {
            changeRequestCreatorByField(userId);
        }

        leftUserRepo.save(Objects.requireNonNull(saveLeftUserData(userId)));

        changeTimeOffRequestStatus(userId);

        return userRepo.removeById(userId);
    }

    private LeftUser saveLeftUserData(Long userId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundUserByIdException(userId)));

        User deletedUser = optionalUser.get();
        LeftUser leftUser = new LeftUser();
        leftUser.setUserId(deletedUser.getId());
        leftUser.setUsername(deletedUser.getUsername());
        leftUser.setDeletionDate(Instant.now());

        return leftUser;
    }

    private void changeTimeOffRequestStatus(Long userId) {
        List<TimeOffRequest> timeList = timeOffRequestRepository.findAllByRequesterId(userId);

        for (TimeOffRequest currentTimeOffRequest : timeList) {
            if (currentTimeOffRequest.getStatus().equals(Status.AWAITING)) {
                currentTimeOffRequest.setStatus(Status.CANCELED);
                this.timeOffRequestRepository.save(currentTimeOffRequest);
            }
        }
    }

    public boolean isUsersCreator(Long userId) {
        int isUserCreator = this.userRepo.userIsUsersCreator(userId);
        return isUserCreator > 0;
    }

    public boolean isTeamsCreator(Long userId) {
        int isTeamsCreator = this.teamRepository.isTeamCreator(userId);
        return isTeamsCreator > 0;
    }

    public boolean isRequestsCreator(Long userId) {
        int isRequestsCreator = this.timeOffRequestRepository.isRequestCreator(userId);
        return isRequestsCreator > 0;
    }

    @Override
    public boolean isTeamLeader(Long userId) {

        int isTeamLeader = this.userRepo.userIsTeamLeader(userId);
        return isTeamLeader > 0;
    }

    private void changeRequestCreatorByField(Long userId) {
        List<TimeOffRequest> requestsToEdit = this.timeOffRequestRepository
                .findTimeOffRequestByCreatorBy(userId);
        User modifier = this.authenticationService.getLoggedUser();
        requestsToEdit.forEach(rq -> {
            rq.setCreatedBy(modifier.getId());
            this.timeOffRequestRepository.save(rq);
        });
    }

    private void changeTeamsCreatorByField(Long userId) {
        List<Team> teamsToEdit = this.teamRepository.findTeamsByCreatorBy(userId);
        User modifier = this.authenticationService.getLoggedUser();

        teamsToEdit.forEach(t -> {
            t.setCreatedBy(modifier.getId());
            this.teamRepository.save(t);
        });
    }

    private void changeUsersCreatorByField(Long userId) {
        List<User> usersToEdit = this.userRepo.findUsersByCreatorBy(userId);
        User modifier = this.authenticationService.getLoggedUser();

        usersToEdit.forEach(u -> {
            u.setCreatedBy(modifier.getId());
            this.userRepo.save(u);
        });
    }

    private Map<LeaveType, Integer> daysOffAllowance() {

        Map<LeaveType, Integer> remainingDays = new HashMap<>();
        for (LeaveType value : LeaveType.values()) {
            remainingDays.put(value, value.getDays());
        }
        return remainingDays;
    }
}

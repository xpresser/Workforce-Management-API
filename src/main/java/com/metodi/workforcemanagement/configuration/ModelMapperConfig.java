package com.metodi.workforcemanagement.configuration;

import com.metodi.workforcemanagement.controllers.dtos.team.TeamResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.RequestShortDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ShortApprovalResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.entities.*;
import com.metodi.workforcemanagement.repositories.TimeOffRequestRepository;
import com.metodi.workforcemanagement.repositories.UserRepository;
import lombok.Getter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
public class ModelMapperConfig {

    private final UserRepository userRepository;
    private final TimeOffRequestRepository timeOffRequestRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ModelMapperConfig(UserRepository userRepository,
                             TimeOffRequestRepository timeOffRequestRepository,
                             ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.timeOffRequestRepository = timeOffRequestRepository;
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void setUp() {
        Converter<Set<TimeOffResponse>, List<ShortApprovalResponseDTO>> responsesToShortDTOConverter = convertShortResponseDTO();

        Converter<Long, UserShortDTO> longToUserShortConverter = convertLongToUserShortDTO();

        Converter<Set<User>, List<UserShortDTO>> userToLongConverter = convertUserToShortDTO();

        Converter<TimeOffRequest, RequestShortDTO> timeOffRequestToRequestShortConverter = convertLongToRequestShortDTO();

        modelMapper.createTypeMap(User.class, UserResponseDTO.class)
                .addMappings(map -> map
                        .using(longToUserShortConverter)
                        .map(
                                Auditable::getCreatedBy,
                                UserResponseDTO::setCreatedBy
                        )
                )
                .addMappings(map -> map
                        .using(longToUserShortConverter)
                        .map(
                                Auditable::getUpdatedBy,
                                UserResponseDTO::setUpdatedBy
                        )
                );

        modelMapper.createTypeMap(Team.class, TeamResponseDTO.class)
                .addMappings(map -> map
                        .using(userToLongConverter)
                        .map(
                                Team::getMembers,
                                TeamResponseDTO::setTeamMembers
                        )
                )
                .addMappings(map -> map
                        .using(longToUserShortConverter)
                        .map(
                                Auditable::getCreatedBy,
                                TeamResponseDTO::setCreatedBy
                        )
                )
                .addMappings(map -> map
                        .using(longToUserShortConverter)
                        .map(
                                Auditable::getUpdatedBy,
                                TeamResponseDTO::setUpdatedBy
                        )
                );

        modelMapper.createTypeMap(TimeOffRequest.class, TimeOffRequestResponseDTO.class)
                .addMappings(map -> map
                        .using(responsesToShortDTOConverter)
                        .map(
                                TimeOffRequest::getResponses,
                                TimeOffRequestResponseDTO::setResponses
                        )
                )
                .addMappings(map -> map
                        .using(longToUserShortConverter)
                        .map(
                                TimeOffRequest::getRequesterId,
                                TimeOffRequestResponseDTO::setRequester
                        )
                )
                .addMappings(map -> map
                        .using(longToUserShortConverter)
                        .map(
                                Auditable::getCreatedBy,
                                TimeOffRequestResponseDTO::setCreatedBy
                        )
                )
                .addMappings(map -> map
                        .using(longToUserShortConverter)
                        .map(
                                Auditable::getUpdatedBy,
                                TimeOffRequestResponseDTO::setUpdatedBy
                        )
                );

        modelMapper.createTypeMap(TimeOffResponse.class, ApprovalResponseDTO.class)
                .addMappings(map -> map
                        .using(timeOffRequestToRequestShortConverter)
                        .map(
                                TimeOffResponse::getRequest,
                                ApprovalResponseDTO::setRequest
                        )
                );
    }

    private Converter<TimeOffRequest, RequestShortDTO> convertLongToRequestShortDTO() {
        return mappingContext -> {
            RequestShortDTO request = new RequestShortDTO();

            Optional<TimeOffRequest> mappedRequest = timeOffRequestRepository.findById(mappingContext.getSource().getId());

            if (mappedRequest.isPresent()) {
                request.setId(mappedRequest.get().getId());
                request.setLeaveType(mappedRequest.get().getLeaveType());
                request.setStatus(mappedRequest.get().getStatus());
                request.setRequester(userRepository.getOne(mappedRequest.get().getRequesterId()).getUsername());
            } else {
                request.setId(mappingContext.getSource().getId());
            }

            return request;
        };
    }

    private Converter<Set<User>, List<UserShortDTO>> convertUserToShortDTO() {
        return ctx -> ctx.getSource()
                .stream()
                .map(user -> modelMapper.map(user, UserShortDTO.class))
                .collect(Collectors.toList());
    }

    private Converter<Long, UserShortDTO> convertLongToUserShortDTO() {
        return mappingContext -> {
            Long userId = mappingContext.getSource();
            UserShortDTO user = new UserShortDTO();

            Optional<User> mappedUser = userRepository.findById(userId);

            if (mappedUser.isPresent()) {
                user.setId(mappedUser.get().getId());
                user.setUsername(mappedUser.get().getUsername());
            } else {
                user.setId(mappingContext.getSource());
                user.setUsername("Deleted User");
            }

            return user;
        };
    }

    private Converter<Set<TimeOffResponse>, List<ShortApprovalResponseDTO>> convertShortResponseDTO() {
        return mappingContext -> {
            List<ShortApprovalResponseDTO> shortApprovalResponses = new ArrayList<>();
            ShortApprovalResponseDTO responsesDTO = new ShortApprovalResponseDTO();

            mappingContext.getSource().forEach(m -> {
                responsesDTO.setId(m.getId());
                responsesDTO.setApprover(m.getApprover().getUsername());
                responsesDTO.setApproved(m.isApproved());
                shortApprovalResponses.add(responsesDTO);
            });

            return shortApprovalResponses;
        };
    }
}

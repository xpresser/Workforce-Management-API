package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_response.ApprovalResponseDTO;
import com.metodi.workforcemanagement.services.TimeOffResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import static com.metodi.workforcemanagement.utils.PageProperties.*;

@RestController
@RequestMapping("/time-off-responses")
@PreAuthorize("@teamLeaderCheck.loggedUserIsTeamLeadOrAdmin()")
public class TimeOffResponseController {
    private final TimeOffResponseService timeOffResponseService;

    @Autowired
    public TimeOffResponseController(TimeOffResponseService timeOffResponseService) {
        this.timeOffResponseService = timeOffResponseService;
    }

    @Operation(
            description = "Gets a list of time off responses"
            , tags = {"time off responses"}
            , summary = "Returns all time of responses"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Bad request."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class)))})
    @GetMapping
    public Page<ApprovalResponseDTO> getAll(
            @RequestParam(name = "page", required = false,defaultValue = DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        return timeOffResponseService.getAll(pageable);
    }

    @Operation(
            description = "Find time off response by ID"
            , tags = {"time off responses"}
            , summary = "Return a single time off response"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApprovalResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Bad request."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class)))})
    @GetMapping("/{responseId}")
    public ApprovalResponseDTO getById(@PathVariable("responseId") Long responseId) {
        return timeOffResponseService.getResponseById(responseId);
    }

    @Operation(
            description = "A time off response registration"
            , tags = {"time off responses"}
            , summary = "Registers a new time off response for a specific time off request"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = TimeOffRequestResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Bad request."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "User is not a approver."
                    , content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusException.class)))})
    @PostMapping("/for-request/{requestId}")
    @PreAuthorize("@teamLeaderCheck.isApprover(#timeOffRequestId)")
    @ResponseStatus(HttpStatus.CREATED)
    public ApprovalResponseDTO create(@Validated @NotBlank
                                      @PathVariable("requestId") Long timeOffRequestId,
                                      @RequestBody @NotEmpty ApprovalRequestDTO approvalRequestDTO) {
        return timeOffResponseService.create(timeOffRequestId, approvalRequestDTO);
    }
}

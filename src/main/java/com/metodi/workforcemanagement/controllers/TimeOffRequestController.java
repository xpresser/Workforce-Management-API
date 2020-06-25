package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.ErrorResponse;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.time_of_request.TimeOffRequestResponseDTO;
import com.metodi.workforcemanagement.services.TimeOffRequestService;
import com.metodi.workforcemanagement.services.exceptions.UnableToDeleteTimeOffRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.metodi.workforcemanagement.utils.PageProperties.DEFAULT_PAGE;
import static com.metodi.workforcemanagement.utils.PageProperties.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/time-off-request")
public class TimeOffRequestController {

    private static final int ZERO = 0;
    private static final int CANCELED = 2;

    private final TimeOffRequestService timeOffRequestService;

    @Autowired
    public TimeOffRequestController(TimeOffRequestService timeOffRequestService) {
        this.timeOffRequestService = timeOffRequestService;
    }

    @Operation(
            description = "Gets a list of time off requests"
            , tags = {"time off requests"}
            , summary = "Returns all time of requests"
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
    public Page<TimeOffRequestResponseDTO> getAll(
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        return timeOffRequestService.getAll(pageable);
    }

    @Operation(
            description = "Find time off request by ID"
            , tags = {"time off requests"}
            , summary = "Return a single time off request"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = TimeOffRequestResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Bad request."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class)))})
    @GetMapping("/{timeOffRequestId}")
    public TimeOffRequestResponseDTO getById(@PathVariable("timeOffRequestId") int timeOffRequestId) {
        return timeOffRequestService.getRequestById(timeOffRequestId);
    }

    @Operation(
            description = "A time off request registration"
            , tags = {"time off requests"}
            , summary = "Registers a new time off request for a specific user"
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
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @PostMapping("/users/{userId}")
    @PreAuthorize("@checkUserIsAdmin.isUserAdmin(#userId)")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeOffRequestResponseDTO createTimeOffRequest(@Valid @NotNull
                                                          @RequestBody TimeOffRequestDTO timeOffRequestDTO
            , @Parameter(
            required = true
            , description = "the User ID to whom the request applies")
                                                          @PathVariable Long userId) {

        return this.timeOffRequestService.create(timeOffRequestDTO, userId);
    }

    @Operation(
            description = "A time off request modification"
            , tags = {"time off requests"}
            , summary = "Edits a specific time off request in the system"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = TimeOffRequestResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Wrong time off request details"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping("/{timeOffRequestId}")
    public TimeOffRequestResponseDTO editTimeOffRequest(@Valid @NotNull @RequestBody TimeOffRequestDTO
                                                                timeOffRequestDTO,
                                                        @PathVariable("timeOffRequestId") Long timeOffRequestId) {
        return this.timeOffRequestService.edit(timeOffRequestId, timeOffRequestDTO);
    }

    @Operation(
            description = "Delete a time off request"
            , tags = {"time off requests"}
            , summary = "Deletes a specific time off request in the system"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json")}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Wrong time off requests id"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @DeleteMapping("/{timeOffRequestId}")
    public ResponseEntity<String> deleteTimeOffRequest(@PathVariable("timeOffRequestId") int timeOffRequestId) {
        int delete = timeOffRequestService.deleteById(timeOffRequestId);

        switch (delete) {
            case ZERO:
                throw new UnableToDeleteTimeOffRequestException("Unsuccessful delete operation!");
            case CANCELED:
                return new ResponseEntity<>("Time Off Request status was set to canceled.\n" +
                        "Your Time Off Request got one or more approval from a Team Lead", HttpStatus.OK);
            default:
                return new ResponseEntity<>("Successfully deleted!", HttpStatus.OK);
        }
    }
}

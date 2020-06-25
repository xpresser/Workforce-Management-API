package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.ErrorResponse;
import com.metodi.workforcemanagement.controllers.dtos.team.TeamRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.team.TeamResponseDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserShortDTO;
import com.metodi.workforcemanagement.services.PostValidation;
import com.metodi.workforcemanagement.services.PutValidation;
import com.metodi.workforcemanagement.services.TeamService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.metodi.workforcemanagement.utils.PageProperties.DEFAULT_PAGE;
import static com.metodi.workforcemanagement.utils.PageProperties.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/teams")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class TeamController {
    private static final int ZERO = 0;
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(
            description = "Gets a list of teams"
            , tags = {"teams"}
            , summary = "Returns all registered teams"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Bad request."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @GetMapping
    public Page<TeamResponseDTO> getAll(
            @RequestParam(name = "page", required = false,defaultValue = DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        return teamService.getAll(pageable);
    }

    @Operation(
            description = "Find team by ID"
            , tags = {"teams"}
            , summary = "Return a single team"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = TeamResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Bad request."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @GetMapping("/{teamId}")
    public TeamResponseDTO getById(@PathVariable("teamId") long teamId) {
        return teamService.getTeamById(teamId);
    }

    @Operation(
            description = "Delete a team"
            , tags = {"teams"}
            , security = {@SecurityRequirement(name = "bearer-key")}
            , summary = "Deletes a specific team in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json")}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Wrong team id"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> delete(@PathVariable("teamId") long teamId) {
        int delete = teamService.deleteById(teamId);
        if (delete == ZERO) {
            return new ResponseEntity<>("Unsuccessful delete operation!.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Successfully deleted!.", HttpStatus.OK);
    }

    @Operation(
            description = "A team registration"
            , tags = {"teams"}
            , security = {@SecurityRequirement(name = "bearer-key")}
            , summary = "Registers a new team in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = TeamResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Bad request."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponseDTO create(@Validated(PostValidation.class) @NotNull @RequestBody TeamRequestDTO teamRequestDTO) {
        return teamService.create(teamRequestDTO);
    }

    @Operation(
            description = "A team modification"
            , tags = {"teams"}
            , security = {@SecurityRequirement(name = "bearer-key")}
            , summary = "Edits a specific team in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = TeamResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Wrong team details"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @PutMapping("/{teamId}")
    public TeamResponseDTO edit(@Validated(PutValidation.class) @NotNull @RequestBody TeamRequestDTO teamRequestDTO,
                                @PathVariable("teamId") long teamId) {

        return teamService.edit(teamId, teamRequestDTO);
    }

    @Operation(
            description = "Adds a member in the teams"
            , tags = {"teams"}
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "Collections of members"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Wrong user or team details"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @PutMapping("/{teamId}/users/{userId}")
    public List<UserShortDTO> assignMember(@PathVariable("teamId") long teamId, @PathVariable("userId") long userId) {
        return teamService.assignMember(userId, teamId);
    }

    @Operation(
            description = "Removes a member in the teams"
            , tags = {"teams"}
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "Collections of members"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Wrong user or team details"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @DeleteMapping("/{teamId}/users/{userId}")
    @PreAuthorize("!@teamLeaderCheck.isTeamLead(#userId, #teamId)")
    public List<UserShortDTO> unassignMember(@PathVariable("teamId") long teamId, @PathVariable("userId") long userId) {
        return teamService.unassignMember( userId, teamId);
    }
}

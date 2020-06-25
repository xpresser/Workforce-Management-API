package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.ErrorResponse;
import com.metodi.workforcemanagement.controllers.dtos.user.UserRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.user.UserResponseDTO;
import com.metodi.workforcemanagement.services.PostValidation;
import com.metodi.workforcemanagement.services.PutValidation;
import com.metodi.workforcemanagement.services.UserService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.constraints.NotNull;

import static com.metodi.workforcemanagement.utils.PageProperties.DEFAULT_PAGE;
import static com.metodi.workforcemanagement.utils.PageProperties.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {
    private static final int ZERO = 0;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            description = "Find user by ID"
            , tags = {"users"}
            , summary = "Return a single user"
            , security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Bad request."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @GetMapping("/{userId}")
    public UserResponseDTO getUserById(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @Operation(
            description = "Gets a list of users"
            , tags = {"users"}
            , summary = "Returns all registered users"
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
    public Page<UserResponseDTO> getAll(
            @Parameter(description = "Page number",example = "0", hidden = true)
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE)
                    Integer page,
            @Parameter(description = "Page size",example = "10", hidden = true)
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE)
                    Integer size ) {

        Pageable pageable = PageRequest.of(page, size);

        return userService.getAll(pageable);
    }

    @Operation(
            description = "A user registration"
            , tags = {"users"}
            , security = {@SecurityRequirement(name = "bearer-key")}
            , summary = "Registers a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))}),
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
    public UserResponseDTO createUser(@Validated(PostValidation.class) @RequestBody UserRequestDTO userRequestDTO) {
        return this.userService.createUser(userRequestDTO);
    }

    @Operation(
            description = "A user modification"
            , tags = {"users"}
            , security = {@SecurityRequirement(name = "bearer-key")}
            , summary = "Edits a specific user in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Wrong user details"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @PutMapping("/{userId}")
    @PreAuthorize("@uniqueUsernameCheck.isUniqueUsername(#userRequestDTO.username,#userId) " +
            "and @uniqueEmailCheck.isUniqueEmail(#userRequestDTO.email, #userId)")
    public UserResponseDTO editUser(@PathVariable("userId") Long userId,
                                    @Validated(PutValidation.class) @NotNull @RequestBody UserRequestDTO userRequestDTO) {
        return userService.updateUser(userId, userRequestDTO);
    }

    @Operation(
            description = "Delete a user"
            , tags = {"users"}
            , security = {@SecurityRequirement(name = "bearer-key")}
            , summary = "Deletes a specific user in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204"
                    , description = "successful operation"
                    , content = {@Content(mediaType = "application/json")}),
            @ApiResponse(
                    responseCode = "400"
                    , description = "Wrong user id"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class))),
            @ApiResponse(
                    responseCode = "403"
                    , description = "Authorization information is missing or invalid."
                    , content = @Content(mediaType = "application/json", schema = @Schema()))})
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        int delete = userService.deleteById(userId);
        if (delete == ZERO) {
            return new ResponseEntity<>("Unsuccessful delete operation!.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Successfully deleted!.", HttpStatus.OK);
    }
}

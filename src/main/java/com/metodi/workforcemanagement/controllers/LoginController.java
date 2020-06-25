package com.metodi.workforcemanagement.controllers;

import com.metodi.workforcemanagement.controllers.dtos.login.LoginRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.login.LoginResponseDTO;
import com.metodi.workforcemanagement.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/login")
public class LoginController {

    private final AuthenticationService authenticationService;

    @Autowired
    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            description = "Login in the API"
            , tags = {"login"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
                    , description = "Bearer Token"
                    , content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "401"
                    , description = "Wrong user user credentials"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseStatusException.class)))})
    @PostMapping
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authenticationService.login(loginRequestDTO);
    }
}

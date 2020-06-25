package com.metodi.workforcemanagement.controllers.dtos.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.metodi.workforcemanagement.services.PostValidation;
import com.metodi.workforcemanagement.services.PutValidation;
import com.metodi.workforcemanagement.services.exceptions.UniqueEmail;
import com.metodi.workforcemanagement.services.exceptions.UniqueUsername;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class UserRequestDTO {

    @Email( message = "error.request.email.not_valid", groups = {PostValidation.class, PutValidation.class})
    @UniqueEmail( message = "error.request.email.unique",groups = {PostValidation.class})
    @NotEmpty(message = "error.request.email.empty", groups = {PostValidation.class, PutValidation.class})
    private String email;

    @NotEmpty(message = "error.request.password.empty", groups = {PostValidation.class, PutValidation.class})
    private String password;

    @UniqueUsername(message = "error.request.username.unique",groups = {PostValidation.class})
    @NotEmpty(message = "error.request.username.empty", groups = {PostValidation.class, PutValidation.class})
    private String username;

    @NotEmpty(message = "error.request.first_name.empty", groups = {PostValidation.class, PutValidation.class})
    private String firstName;

    @NotEmpty(message = "error.request.last_name.empty", groups = {PostValidation.class, PutValidation.class})
    private String lastName;

    @JsonProperty("isAdmin")
    private boolean isAdmin;

    @JsonProperty("isOnLeave")
    private boolean isOnLeave;
}

package com.metodi.workforcemanagement.services;

import com.metodi.workforcemanagement.controllers.dtos.login.LoginRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.login.LoginResponseDTO;
import com.metodi.workforcemanagement.entities.User;

public interface AuthenticationService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    boolean userIsLoggedIn();

    User getLoggedUser();
}

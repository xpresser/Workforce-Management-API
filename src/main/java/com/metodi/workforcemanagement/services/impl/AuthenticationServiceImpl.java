package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.controllers.dtos.login.LoginRequestDTO;
import com.metodi.workforcemanagement.controllers.dtos.login.LoginResponseDTO;
import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.security.UserPrincipal;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.TokenService;
import com.metodi.workforcemanagement.services.exceptions.NotFoundUserByEmailException;
import com.metodi.workforcemanagement.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final TokenService tokenService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,
                                     TokenService tokenService,
                                     BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        User user = userRepository
                .findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundUserByEmailException("No user with email and password"));

        if (!bCryptPasswordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new NotFoundUserByEmailException("No user with email and password");
        }

        return createLoginResponseDTO(user);
    }

    @Override
    public boolean userIsLoggedIn() {
        Optional<Authentication> authentication =  Optional.ofNullable(
                SecurityContextHolder
                        .getContext().getAuthentication());

                return authentication.isPresent();
    }

    @Override
    public User getLoggedUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User is not in the database"));
    }

    private LoginResponseDTO createLoginResponseDTO(User user) {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();

        loginResponseDTO.setToken(
                tokenService.generateToken(user)
        );

        return loginResponseDTO;
    }
}

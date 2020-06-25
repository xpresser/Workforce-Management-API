package com.metodi.workforcemanagement.utils;

import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.AuthenticationService;
import com.metodi.workforcemanagement.services.exceptions.NotFoundUserByIdException;
import com.metodi.workforcemanagement.services.exceptions.UnauthorizedUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("checkUserIsAdmin")
public class CheckUserIsAdmin {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepo;

    @Autowired
    public CheckUserIsAdmin(AuthenticationService authenticationService, UserRepository userRepo) {
        this.authenticationService = authenticationService;
        this.userRepo = userRepo;
    }

    public boolean isUserAdmin(Long userId) {
       User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundUserByIdException(userId));
        User logUser = authenticationService.getLoggedUser();
        if (!user.getId().equals(logUser.getId())){
            if (!logUser.isAdmin()) {
                throw new UnauthorizedUserException("User must be admin or creator!!!");
            }
        }

        return true;
    }
}

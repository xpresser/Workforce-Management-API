package com.metodi.workforcemanagement.utils;

import com.metodi.workforcemanagement.repositories.UserRepository;
import com.metodi.workforcemanagement.services.exceptions.NotUniqueUsernameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("uniqueUsernameCheck")
public class UniqueUsernameCheck {

    private final UserRepository userRepository;

    @Autowired
    public UniqueUsernameCheck(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean isUniqueUsername(String username, Long userId){
        Boolean existsUsername = this.userRepository.existsByUsername(username);
        Boolean existsUsernameAndId = this.userRepository.existsByUsernameAndId(username, userId);

        if (!existsUsername){
            return Boolean.TRUE;
        }

        if (!existsUsernameAndId){
            throw new NotUniqueUsernameException();
        }

        return true;
    }
}

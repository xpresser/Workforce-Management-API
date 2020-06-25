package com.metodi.workforcemanagement.configuration;

import com.metodi.workforcemanagement.security.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public class EntityAuditorAware implements AuditorAware<Long> {

    private static final long DEFAULT_USER_ID = 1L;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Optional<Long> loggedUserId;
        Optional<Authentication> authentication =  Optional.ofNullable(
                SecurityContextHolder
                        .getContext().getAuthentication()
        );
        if (authentication.isPresent()){
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            loggedUserId =  Optional.of(userPrincipal.getId());
        }else{
            loggedUserId = Optional.of(DEFAULT_USER_ID);
        }
        return loggedUserId;
    }
}


package com.metodi.workforcemanagement.services;

import com.metodi.workforcemanagement.entities.User;
import com.metodi.workforcemanagement.security.UserPrincipal;

public interface TokenService {

    String generateToken(User user);

    UserPrincipal parseToken(String token);
}

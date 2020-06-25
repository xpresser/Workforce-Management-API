package com.metodi.workforcemanagement.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPrincipal {

    private long id;

    private String username;

    private String email;

    private boolean isAdmin;

    public UserPrincipal(long id, String username, String email, boolean isAdmin) {
        this.setId(id);
        this.setUsername(username);
        this.setEmail(email);
        this.setAdmin(isAdmin);
    }
}

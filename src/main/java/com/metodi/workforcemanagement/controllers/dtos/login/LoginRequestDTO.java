package com.metodi.workforcemanagement.controllers.dtos.login;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestDTO {

    private String email;

    private String password;
}

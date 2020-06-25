package com.metodi.workforcemanagement.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "This user currently is a team leader. Please first change his role" )
public class UserTeamLeaderRoleException extends RuntimeException{}

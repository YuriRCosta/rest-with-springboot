package br.com.yuri.restwithspringboot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidJWTAuthenticationException extends AuthenticationException {

    public InvalidJWTAuthenticationException(String exception) {
        super(exception);
    }

}

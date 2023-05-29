package br.com.yuri.restwithspringboot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectIsNullException extends RuntimeException{

    public RequiredObjectIsNullException(String exception) {
        super(exception);
    }

    public RequiredObjectIsNullException() {
        super("Required object is not allowed to be null");
    }

}

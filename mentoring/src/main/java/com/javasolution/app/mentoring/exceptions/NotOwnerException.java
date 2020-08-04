package com.javasolution.app.mentoring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotOwnerException extends RuntimeException {

    public NotOwnerException(String message) {
        super(message);
    }
}

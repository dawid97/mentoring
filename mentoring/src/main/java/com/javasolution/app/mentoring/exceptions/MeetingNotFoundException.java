package com.javasolution.app.mentoring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MeetingNotFoundException extends RuntimeException {

    public MeetingNotFoundException(final String message) {
        super(message);
    }
}

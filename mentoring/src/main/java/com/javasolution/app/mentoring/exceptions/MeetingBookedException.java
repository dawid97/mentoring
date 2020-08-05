package com.javasolution.app.mentoring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MeetingBookedException extends RuntimeException {

    public MeetingBookedException(final String message) {
        super(message);
    }
}

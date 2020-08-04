package com.javasolution.app.mentoring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MeetingTimeException extends RuntimeException {

    public MeetingTimeException(String message) {
        super(message);
    }
}

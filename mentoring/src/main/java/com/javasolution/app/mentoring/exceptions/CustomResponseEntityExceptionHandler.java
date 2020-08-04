package com.javasolution.app.mentoring.exceptions;

import com.javasolution.app.mentoring.responses.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;

@ControllerAdvice
//@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<Object> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, WebRequest request) {
        final UsernameAlreadyExistsResponse exceptionResponse = new UsernameAlreadyExistsResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleUnableSendEmail(UnableSendEmailException ex, WebRequest request) {
        final UnableSendEmailResponse exceptionResponse = new UnableSendEmailResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleInvalidCast(InvalidCastException ex, WebRequest request) {
        final InvalidCastResponse exceptionResponse = new InvalidCastResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        final UserNotFoundResponse exceptionResponse = new UserNotFoundResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleDeleteAccount(DeleteAccountException ex, WebRequest request) {
        final DeleteAccountResponse exceptionResponse = new DeleteAccountResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingBookingAlreadyExists(MeetingBookingAlreadyExistsException ex, WebRequest request) {
        final MeetingBookingAlreadyExistsResponse exceptionResponse = new MeetingBookingAlreadyExistsResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMentorNotFound(MentorNotFoundException ex, WebRequest request) {
        final MentorNotFoundResponse exceptionResponse = new MentorNotFoundResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingsAlreadyExist(MeetingsAlreadyExistException ex, WebRequest request) {
        final MeetingsAlreadyExistResponse exceptionResponse = new MeetingsAlreadyExistResponse(ex.getMessage(), Collections.singletonList(ex.getCollisionMeetings()));
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingNotFound(MeetingNotFoundException ex, WebRequest request) {
        final MeetingNotFoundResponse exceptionResponse = new MeetingNotFoundResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingBooked(MeetingBookedException ex, WebRequest request) {
        final MeetingBookedResponse exceptionResponse = new MeetingBookedResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingTime(MeetingTimeException ex, WebRequest request) {
        final MeetingTimeResponse exceptionResponse = new MeetingTimeResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}

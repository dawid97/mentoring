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
    public final ResponseEntity<Object> handleUsernameAlreadyExists(final UsernameAlreadyExistsException ex, final WebRequest request) {
        final UsernameAlreadyExistsResponse exceptionResponse = new UsernameAlreadyExistsResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleUnableSendEmail(final UnableSendEmailException ex, final WebRequest request) {
        final UnableSendEmailResponse exceptionResponse = new UnableSendEmailResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleInvalidCast(final InvalidCastException ex, final WebRequest request) {
        final InvalidCastResponse exceptionResponse = new InvalidCastResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleUserNotFound(final UserNotFoundException ex, final WebRequest request) {
        final UserNotFoundResponse exceptionResponse = new UserNotFoundResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleDeleteAccount(final DeleteAccountException ex, final WebRequest request) {
        final DeleteAccountResponse exceptionResponse = new DeleteAccountResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingBookingAlreadyExists(final MeetingBookingAlreadyExistsException ex, final WebRequest request) {
        final MeetingBookingAlreadyExistsResponse exceptionResponse = new MeetingBookingAlreadyExistsResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMentorNotFound(final MentorNotFoundException ex, final WebRequest request) {
        final MentorNotFoundResponse exceptionResponse = new MentorNotFoundResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingsAlreadyExist(final MeetingsAlreadyExistException ex, final WebRequest request) {
        final MeetingsAlreadyExistResponse exceptionResponse = new MeetingsAlreadyExistResponse(ex.getMessage(), Collections.singletonList(ex.getCollisionMeetings()));
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingNotFound(final MeetingNotFoundException ex, final WebRequest request) {
        final MeetingNotFoundResponse exceptionResponse = new MeetingNotFoundResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingBooked(final MeetingBookedException ex, final WebRequest request) {
        final MeetingBookedResponse exceptionResponse = new MeetingBookedResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingTime(final MeetingTimeException ex, final WebRequest request) {
        final MeetingTimeResponse exceptionResponse = new MeetingTimeResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleMeetingBookingNotFound(final MeetingBookingNotFoundException ex, final WebRequest request) {
        final MeetingBookingNotFoundResponse exceptionResponse = new MeetingBookingNotFoundResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleNotOwner(final NotOwnerException ex, final WebRequest request) {
        final NotOwnerResponse exceptionResponse = new NotOwnerResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}

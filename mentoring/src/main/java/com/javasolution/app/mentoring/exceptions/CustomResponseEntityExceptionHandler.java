package com.javasolution.app.mentoring.exceptions;

import com.javasolution.app.mentoring.responses.InvalidCastResponse;
import com.javasolution.app.mentoring.responses.UnableSendEmailResponse;
import com.javasolution.app.mentoring.responses.UserNotFoundResponse;
import com.javasolution.app.mentoring.responses.UsernameAlreadyExistsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
}

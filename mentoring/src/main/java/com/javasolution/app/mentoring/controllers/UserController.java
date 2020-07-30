package com.javasolution.app.mentoring.controllers;

import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.services.MapValidationErrorService;
import com.javasolution.app.mentoring.services.UserService;
import com.javasolution.app.mentoring.validators.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final MapValidationErrorService mapValidationErrorService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {

        userValidator.validate(user,result);
        final ResponseEntity<?> errorMap= mapValidationErrorService.mapValidationService(result);

        if(errorMap!=null) return errorMap;

        final User newUser = userService.signUpUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}

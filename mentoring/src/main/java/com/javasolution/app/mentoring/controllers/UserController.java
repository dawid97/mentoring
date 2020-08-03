package com.javasolution.app.mentoring.controllers;

import com.javasolution.app.mentoring.entities.ConfirmationToken;
import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.requests.LoginRequest;
import com.javasolution.app.mentoring.requests.UpdateUserRequest;
import com.javasolution.app.mentoring.responses.LoginResponse;
import com.javasolution.app.mentoring.security.JwtUtil;
import com.javasolution.app.mentoring.services.ConfirmationTokenService;
import com.javasolution.app.mentoring.services.MapValidationErrorService;
import com.javasolution.app.mentoring.services.UserService;
import com.javasolution.app.mentoring.validators.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserValidator userValidator;
    private final MapValidationErrorService mapValidationErrorService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@Valid @RequestBody UpdateUserRequest updateUserRequest,
                                      BindingResult result,
                                      Principal principal) {

        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        final User user = userService.updateMe(updateUserRequest, principal);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) throws Exception {

        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);

        if (errorMap != null) return errorMap;

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @GetMapping("/sign-up/confirm")
    ResponseEntity<?> confirmMail(@RequestParam("token") String token) {

        final Optional<ConfirmationToken> optionalConfirmationToken = confirmationTokenService.findConfirmationTokenByToken(token);
        optionalConfirmationToken.ifPresent(userService::confirmUser);
        return new ResponseEntity<>("verified email", HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {

        userValidator.validate(user, result);
        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);

        if (errorMap != null) return errorMap;

        final User newUser = userService.signUpUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}

package com.javasolution.app.mentoring.controllers;

import com.javasolution.app.mentoring.entities.ConfirmationToken;
import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.requests.LoginRequest;
import com.javasolution.app.mentoring.requests.UpdateUserRequest;
import com.javasolution.app.mentoring.responses.DeleteAccountResponse;
import com.javasolution.app.mentoring.responses.LoginResponse;
import com.javasolution.app.mentoring.security.JwtUtil;
import com.javasolution.app.mentoring.services.ConfirmationTokenService;
import com.javasolution.app.mentoring.services.MapValidationErrorService;
import com.javasolution.app.mentoring.services.UserService;
import com.javasolution.app.mentoring.validators.UserValidator;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Update Me Web Service Endpoint")
    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@Valid @RequestBody final UpdateUserRequest updateUserRequest,
                                      final BindingResult result,
                                      final Principal principal) {

        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        final User user = userService.updateMe(updateUserRequest, principal);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Delete User Web Service Endpoint")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable final String userId) {

        userService.deleteUser(userId);

        return new ResponseEntity<>(new DeleteAccountResponse("Account with ID: '" + userId + "' deleted successfully"), HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Delete Account Web Service Endpoint")
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(final Principal principal) {

        userService.deleteAccount(principal);

        return new ResponseEntity<>(new DeleteAccountResponse("Account deleted successfully"), HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get Me Web Service Endpoint")
    @GetMapping("/me")
    public ResponseEntity<?> getMe(final Principal principal) {

        final User user = userService.getMe(principal);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "The Sign-in User Web Service Endpoint")
    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody final LoginRequest loginRequest, final BindingResult result) throws Exception {

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

    @ApiOperation(value = "The Confirm Mail Web Service Endpoint")
    @GetMapping("/sign-up/confirm")
    ResponseEntity<?> confirmMail(@RequestParam("token") final String token) {

        final Optional<ConfirmationToken> optionalConfirmationToken = confirmationTokenService.findConfirmationTokenByToken(token);
        optionalConfirmationToken.ifPresent(userService::confirmUser);
        return new ResponseEntity<>("verified email", HttpStatus.OK);
    }

    @ApiOperation(value = "The Sign-up User Web Service Endpoint")
    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody final User user, final BindingResult result) {

        userValidator.validate(user, result);
        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);

        if (errorMap != null) return errorMap;

        final User newUser = userService.signUpUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get All Users Web Service Endpoint")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {

        final Iterable<User> users = userService.getAllUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get User Web Service Endpoint")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable final String userId) {

        final User user = userService.getUser(userId);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}

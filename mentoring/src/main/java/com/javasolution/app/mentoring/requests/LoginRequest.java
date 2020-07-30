package com.javasolution.app.mentoring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email can not be blank")
    @Email(message = "Username needs to be an email")
    private String email;

    @NotBlank(message = "Password can not be blank")
    private String password;
}

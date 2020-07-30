package com.javasolution.app.mentoring.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidLoginResponse {

    private String email;
    private String password;

    public InvalidLoginResponse() {
        this.email = "Invalid Email";
        this.password = "Invalid Password";
    }
}

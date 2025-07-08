package com.example.auth_service.web.rest.util;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString(exclude = {"password"})
public class LoginRequest {
    @NotNull
    private String username;

    @NotNull
    private String password;
}

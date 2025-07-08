package com.example.auth_service.service.errors;

public class RoleNotFoundException extends Exception {

    public RoleNotFoundException() {
        super("No existe el rol.");
    }
}

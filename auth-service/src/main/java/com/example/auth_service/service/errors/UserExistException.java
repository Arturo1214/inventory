package com.example.auth_service.service.errors;


public class UserExistException extends Exception {
    public UserExistException() {
        super("El nombre de usuario ya existe");
    }
}

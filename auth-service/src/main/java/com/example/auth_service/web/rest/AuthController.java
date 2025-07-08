package com.example.auth_service.web.rest;


import com.example.auth_service.domain.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.JwtUtil;
import com.example.auth_service.web.rest.errors.BaseAlertException;
import com.example.auth_service.web.rest.util.LoginRequest;
import com.example.auth_service.web.rest.util.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Status;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public AuthController( UserRepository userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {

        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new BaseAlertException("AUTHENTICATE", "Usuario o contraseña invalido.", Status.UNAUTHORIZED));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new BaseAlertException(
                    "AUTHENTICATE",
                    "Usuario o contraseña inválido.",
                    Status.UNAUTHORIZED
            );
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());
        return ResponseEntity.ok(new LoginResponse(token));

    }
}

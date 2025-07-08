package com.example.auth_service.web.rest;


import com.example.auth_service.domain.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.JwtUtil;
import com.example.auth_service.web.rest.errors.BaseAlertException;
import com.example.auth_service.web.rest.util.LoginRequest;
import com.example.auth_service.web.rest.util.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Status;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        log.info("Login request: {}", req);
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

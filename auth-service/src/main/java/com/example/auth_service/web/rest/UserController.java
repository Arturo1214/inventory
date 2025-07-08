package com.example.auth_service.web.rest;

import com.example.auth_service.domain.User;
import com.example.auth_service.service.UserService;
import com.example.auth_service.service.dto.CreateUserDto;
import com.example.auth_service.service.errors.RoleNotFoundException;
import com.example.auth_service.service.errors.UserExistException;
import com.example.auth_service.web.rest.errors.BaseAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Status;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Crea un nuevo usuario",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserDto dto) {
        log.info("Create user: {}", dto);
        try {
            return ResponseEntity.ok(userService.create(dto));
        } catch (UserExistException e) {
            throw new BaseAlertException("USER_EXIST", e.getMessage(), Status.BAD_REQUEST);
        } catch (RoleNotFoundException e) {
            throw new BaseAlertException("ROLE_NOT_FOUND", e.getMessage(), Status.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Lista todos los usuarios",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<User>> list(@ParameterObject Pageable pageable) {
        log.info("List all users: {}", pageable);
        return ResponseEntity.ok(userService.findAll(pageable));
    }
}

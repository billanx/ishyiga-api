package com.ishyiga.controller;

import com.ishyiga.dto.AuthRequest;
import com.ishyiga.entities.User;
import com.ishyiga.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Users", description = "User management APIs")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User Login", description = "Login to get an authentication token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request.getUsername(), request.getPassword()));
    }

    @Operation(summary = "Register User", description = "Register a new user with a specified role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.register(request.getUsername(), request.getPassword(), request.getRole()));
    }
}

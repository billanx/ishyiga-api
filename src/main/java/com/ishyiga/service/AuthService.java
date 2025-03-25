package com.ishyiga.service;

import com.ishyiga.dto.AuthRequest;
import com.ishyiga.dto.AuthResponse;
import com.ishyiga.entities.User;
import com.ishyiga.enums.Role;
import com.ishyiga.repo.UserRepository;
import com.ishyiga.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));
            
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            
            return AuthResponse.builder()
                .success(true)
                .message("Authentication successful")
                .token(token)
                .role(user.getRole().name())
                .username(user.getUsername())
                .build();
                
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user {}: Invalid credentials", request.getUsername());
            return AuthResponse.builder()
                .success(false)
                .message("Invalid username or password")
                .build();
        } catch (Exception e) {
            log.error("Authentication error for user {}: {}", request.getUsername(), e.getMessage());
            return AuthResponse.builder()
                .success(false)
                .message("Authentication failed: " + e.getMessage())
                .build();
        }
    }

    public AuthResponse register(AuthRequest request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return AuthResponse.builder()
                    .success(false)
                    .message("Username already exists")
                    .build();
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            
            User savedUser = userRepository.save(user);
            String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole());
            
            return AuthResponse.builder()
                .success(true)
                .message("User registered successfully")
                .token(token)
                .role(savedUser.getRole().name())
                .username(savedUser.getUsername())
                .build();
                
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return AuthResponse.builder()
                .success(false)
                .message("Registration failed: " + e.getMessage())
                .build();
        }
    }
}

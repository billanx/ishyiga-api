package com.ishyiga.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishyiga.dto.AuthResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String token = authHeader.substring(7);
            if (token.isEmpty()) {
                handleAuthenticationError(response, "Invalid token or user not found");
                return;
            }

            try {
                final String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("User authenticated successfully: {} with roles: {}", username, userDetails.getAuthorities());
                        filterChain.doFilter(request, response);
                    } else {
                        handleAuthenticationError(response, "Invalid or expired token");
                    }
                } else {
                    handleAuthenticationError(response, "Invalid token or user not found");
                }
            } catch (Exception e) {
                log.error("Token validation error: {}", e.getMessage());
                handleAuthenticationError(response, "Invalid or expired token");
            }
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            handleAuthenticationError(response, "Authentication failed");
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            AuthResponse errorResponse = AuthResponse.builder()
                    .success(false)
                    .message(message)
                    .build();
            
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }
    }
}

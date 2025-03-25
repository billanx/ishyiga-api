package com.ishyiga.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishyiga.dto.AuthResponse;
import com.ishyiga.util.JwtAuthenticationFilter;
import com.ishyiga.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import java.util.Arrays;

@Configuration
@Slf4j
public class SecurityConfig {

    private final JwtUtil jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtUtil jwtService, UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService, objectMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter...");

        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
                .disable())
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // INVOICES
                .requestMatchers(HttpMethod.GET, "/api/v1/invoices/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.POST, "/api/v1/invoices/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/invoices/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/invoices/**").hasAnyRole("ADMIN", "ISHYIGA")
                // SALES
                .requestMatchers(HttpMethod.GET, "/api/v1/sales/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.POST, "/api/v1/sales/**").hasAnyRole("ADMIN","ISHYIGA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/sales/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/sales/**").hasRole("ADMIN")
                // PURCHASES
                .requestMatchers(HttpMethod.GET, "/api/v1/purchases/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.POST, "/api/v1/purchases/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/purchases/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/purchases/**").hasRole("ADMIN")
                // STOCK
                .requestMatchers(HttpMethod.GET, "/api/v1/stocks/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.POST, "/api/v1/stocks/**").hasAnyRole("ADMIN","ISHYIGA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/stocks/**").hasAnyRole("ADMIN","ISHYIGA")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/stocks/**").hasRole("ADMIN")
                // LISTITEM
                .requestMatchers(HttpMethod.GET, "/api/v1/listitem/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.POST, "/api/v1/listitem/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/listitem/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/listitem/**").hasRole("ADMIN")
                // ORDER
                .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.POST, "/api/v1/orders/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/orders/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasRole("ADMIN")
                // ITEM
                .requestMatchers(HttpMethod.GET, "/api/v1/items/**").hasAnyRole("ADMIN", "ISHYIGA", "BANK")
                .requestMatchers(HttpMethod.POST, "/api/v1/items/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/items/**").hasAnyRole("ADMIN", "ISHYIGA")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/items/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    
                    AuthResponse errorResponse = AuthResponse.builder()
                        .success(false)
                        .message("Authentication required")
                        .build();
                    
                    objectMapper.writeValue(response.getWriter(), errorResponse);
                })
                .accessDeniedHandler((request, response, e) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    
                    AuthResponse errorResponse = AuthResponse.builder()
                        .success(false)
                        .message("Access denied: Insufficient permissions")
                        .build();
                    
                    objectMapper.writeValue(response.getWriter(), errorResponse);
                }))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(false);
        firewall.setAllowUrlEncodedPercent(false);
        firewall.setAllowUrlEncodedPeriod(false);
        return firewall;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}

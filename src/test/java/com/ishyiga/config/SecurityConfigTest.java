package com.ishyiga.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishyiga.dto.AuthRequest;
import com.ishyiga.dto.AuthResponse;
import com.ishyiga.entities.User;
import com.ishyiga.enums.Role;
import com.ishyiga.repo.UserRepository;
import com.ishyiga.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String ishyigaToken;
    private String bankToken;

    @BeforeEach
    void setUp() {
        // Clear existing users
        userRepository.deleteAll();

        // Create test users
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        User ishyiga = new User();
        ishyiga.setUsername("ishyiga");
        ishyiga.setPassword(passwordEncoder.encode("ishyiga123"));
        ishyiga.setRole(Role.ISHYIGA);
        userRepository.save(ishyiga);

        User bank = new User();
        bank.setUsername("bank");
        bank.setPassword(passwordEncoder.encode("bank123"));
        bank.setRole(Role.BANK);
        userRepository.save(bank);

        // Generate tokens
        adminToken = "Bearer " + jwtUtil.generateToken("admin", Role.ADMIN);
        ishyigaToken = "Bearer " + jwtUtil.generateToken("ishyiga", Role.ISHYIGA);
        bankToken = "Bearer " + jwtUtil.generateToken("bank", Role.BANK);
    }

    @Test
    void testInvoicesEndpoints() throws Exception {
        // Test ADMIN access
        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/invoices")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        // Test ISHYIGA access
        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", ishyigaToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/invoices")
                .header("Authorization", ishyigaToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        // Test BANK access (only GET allowed)
        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", bankToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/invoices")
                .header("Authorization", bankToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testSalesEndpoints() throws Exception {
        // Test ADMIN access
        mockMvc.perform(get("/api/v1/sales")
                .header("Authorization", adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/sales")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        // Test ISHYIGA access
        mockMvc.perform(get("/api/v1/sales")
                .header("Authorization", ishyigaToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/sales")
                .header("Authorization", ishyigaToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());

        // Test BANK access
        mockMvc.perform(get("/api/v1/sales")
                .header("Authorization", bankToken))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/sales/1")
                .header("Authorization", bankToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidToken() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testForbiddenAccess() throws Exception {
        // Try to access admin-only endpoint with ISHYIGA role
        mockMvc.perform(get("/api/v1/sales")
                .header("Authorization", ishyigaToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access denied: Insufficient permissions"));
    }

    @Test
    void testValidAccess() throws Exception {
        // Test access to invoices with ISHYIGA role
        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", ishyigaToken))
                .andExpect(status().isOk());

        // Test access to orders with WHOLESALER role
        mockMvc.perform(get("/api/v1/orders")
                .header("Authorization", ishyigaToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMissingToken() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid token or user not found"));
    }

    @Test
    void testPublicEndpoints() throws Exception {
        // Test access to health check endpoint
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk());

        // Test access to Swagger UI
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        // Test access to login endpoint
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // Since user doesn't exist
    }
}
package com.ishyiga.dto;

import com.ishyiga.enums.Role;
import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private Role role;
}


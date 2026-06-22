package com.ganesh.creatorflow.dto;

import com.ganesh.creatorflow.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Role role;
}

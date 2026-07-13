package com.ganesh.creatorflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthServiceResponse {

    private String token;
    private String role;
}

package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.AuthResponse;
import com.ganesh.creatorflow.dto.AuthServiceResponse;
import com.ganesh.creatorflow.dto.RegisterRequest;
import com.ganesh.creatorflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.ganesh.creatorflow.dto.LoginRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(
            @RequestBody RegisterRequest request
    ) {
        AuthServiceResponse serviceResponse = authService.register(request);
        return new AuthResponse(
                serviceResponse.getToken(),
                serviceResponse.getRole()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {
        AuthServiceResponse serviceResponse = authService.login(request);
        return new AuthResponse(
                serviceResponse.getToken(),
                serviceResponse.getRole()
        );
    }
}

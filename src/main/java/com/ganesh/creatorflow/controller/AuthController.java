package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.AuthResponse;
import com.ganesh.creatorflow.dto.RegisterRequest;
import com.ganesh.creatorflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(
            @RequestBody RegisterRequest request
    ) {
        return new AuthResponse(
                authService.register(request)
        );
    }
}

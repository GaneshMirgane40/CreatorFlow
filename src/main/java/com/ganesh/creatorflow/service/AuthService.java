package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.RegisterRequest;
import com.ganesh.creatorflow.dto.AuthServiceResponse;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ganesh.creatorflow.dto.LoginRequest;
import com.ganesh.creatorflow.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceResponse register(RegisterRequest request) {

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        String role = user.getRole().toString();

        return new AuthServiceResponse(token, role);
    }

    public AuthServiceResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid credentials"
            );
        }

        String token = jwtService.generateToken(user.getEmail());
        String role = user.getRole().toString();

        return new AuthServiceResponse(token, role);
    }
}

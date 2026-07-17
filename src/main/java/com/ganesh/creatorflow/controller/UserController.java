package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.UserResponse;
import com.ganesh.creatorflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/editors")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<List<UserResponse>> getAllEditors() {
        return ResponseEntity.ok(userService.getAllEditors());
    }
}
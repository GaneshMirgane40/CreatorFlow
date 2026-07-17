package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.UserResponse;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.Role;
import com.ganesh.creatorflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllEditors() {
        return userRepository.findByRole(Role.EDITOR)
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .toList();
    }
}
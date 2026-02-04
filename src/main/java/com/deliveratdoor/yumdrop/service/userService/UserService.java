package com.deliveratdoor.yumdrop.service.userService;

import com.deliveratdoor.yumdrop.dto.user.AuthResponse;
import com.deliveratdoor.yumdrop.dto.user.LoginRequest;
import com.deliveratdoor.yumdrop.dto.user.RegisterUserRequest;
import com.deliveratdoor.yumdrop.dto.user.UpdateUserRequest;
import com.deliveratdoor.yumdrop.entity.user.UserEntity;
import com.deliveratdoor.yumdrop.exception.ResourceNotFoundException;
import com.deliveratdoor.yumdrop.model.user.UserRole;
import com.deliveratdoor.yumdrop.repositories.user.UserRepository;
import com.deliveratdoor.yumdrop.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);
    }

    public void updateUser(Long id, UpdateUserRequest request) {
        UserEntity user = getById(id);
        if (Objects.nonNull(request.getUsername())) user.setUsername(request.getUsername());
        if (Objects.nonNull(request.getPassword())) user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        if (Objects.nonNull(request.getEmail())) user.setEmail(request.getEmail());
        if (Objects.nonNull(request.getRole())) user.setRole(request.getRole());
        if (Objects.nonNull(request.getFirstName())) user.setFirstName(request.getFirstName());
        if (Objects.nonNull(request.getLastName())) user.setLastName(request.getLastName());
        if (Objects.nonNull(request.getPhoneNumber())) user.setUsername(request.getPhoneNumber());

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("User is disabled");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }

    public UserEntity getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}


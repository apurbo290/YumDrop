package com.deliveratdoor.yumdrop.service.userService;

import com.deliveratdoor.yumdrop.dto.user.AuthResponse;
import com.deliveratdoor.yumdrop.dto.user.LoginRequest;
import com.deliveratdoor.yumdrop.dto.user.RegisterUserRequest;
import com.deliveratdoor.yumdrop.dto.user.UpdateUserRequest;
import com.deliveratdoor.yumdrop.entity.user.RefreshTokenEntity;
import com.deliveratdoor.yumdrop.entity.user.UserEntity;
import com.deliveratdoor.yumdrop.exception.BadRequestException;
import com.deliveratdoor.yumdrop.exception.ResourceNotFoundException;
import com.deliveratdoor.yumdrop.repositories.user.RefreshTokenRepository;
import com.deliveratdoor.yumdrop.repositories.user.UserRepository;
import com.deliveratdoor.yumdrop.util.auth.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
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
        if (Objects.nonNull(request.getPhoneNumber())) user.setPhoneNumber(request.getPhoneNumber()); // fixed bug
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }
        if (!user.isActive()) {
            throw new BadRequestException("User is disabled");
        }

        String accessToken = jwtUtil.generateToken(user);
        String rawRefresh = jwtUtil.generateRefreshToken();

        // Revoke old refresh tokens for this user
        refreshTokenRepository.deleteAllByUserId(user.getId());

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setToken(rawRefresh);
        refreshToken.setUserId(user.getId());
        refreshToken.setExpiresAt(jwtUtil.refreshTokenExpiresAt());
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, rawRefresh);
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        RefreshTokenEntity stored = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token expired or revoked");
        }

        UserEntity user = getById(stored.getUserId());
        String newAccessToken = jwtUtil.generateToken(user);
        String newRawRefresh = jwtUtil.generateRefreshToken();

        // Rotate refresh token
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        RefreshTokenEntity newRefresh = new RefreshTokenEntity();
        newRefresh.setToken(newRawRefresh);
        newRefresh.setUserId(user.getId());
        newRefresh.setExpiresAt(jwtUtil.refreshTokenExpiresAt());
        refreshTokenRepository.save(newRefresh);

        return new AuthResponse(newAccessToken, newRawRefresh);
    }

    public UserEntity getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}


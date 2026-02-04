package com.deliveratdoor.yumdrop.controler.userControler;

import com.deliveratdoor.yumdrop.dto.user.AuthResponse;
import com.deliveratdoor.yumdrop.dto.user.LoginRequest;
import com.deliveratdoor.yumdrop.dto.user.RegisterUserRequest;
import com.deliveratdoor.yumdrop.dto.user.UpdateUserRequest;
import com.deliveratdoor.yumdrop.entity.user.UserEntity;
import com.deliveratdoor.yumdrop.service.userService.UserService;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register user
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterUserRequest request) {
        userService.register(request);
    }

    @PutMapping("/users/{id}")
    public void update(@Nonnull @PathVariable Long id, @RequestBody UpdateUserRequest request) {
        userService.updateUser(id, request);
    }

    // Login user
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    // (Optional) Get user profile (secured later)
    @GetMapping("/users/{id}")
    public UserEntity getUser(@PathVariable Long id) {
        return userService.getById(id);
    }
}


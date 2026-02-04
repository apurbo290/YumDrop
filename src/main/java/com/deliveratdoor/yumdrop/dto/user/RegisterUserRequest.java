package com.deliveratdoor.yumdrop.dto.user;

import com.deliveratdoor.yumdrop.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    @NotBlank
    @Pattern(regexp = "^\\d{10}$")
    private String phoneNumber;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private UserRole role;
}

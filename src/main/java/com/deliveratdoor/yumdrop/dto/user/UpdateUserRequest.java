package com.deliveratdoor.yumdrop.dto.user;

import com.deliveratdoor.yumdrop.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    @Pattern(regexp = "^\\d{10}$")
    private String phoneNumber;
    @Email
    private String email;
    @Size(min = 6)
    private String password;
    private UserRole role;
}

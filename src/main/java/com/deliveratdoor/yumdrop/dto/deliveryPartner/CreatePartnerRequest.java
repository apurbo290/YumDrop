package com.deliveratdoor.yumdrop.dto.deliveryPartner;

import com.deliveratdoor.yumdrop.model.PartnerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class CreatePartnerRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Pattern(regexp = "^\\d{10}$")
    @Size(min = 10, max = 10)
    private String phone;
    @Email
    private String email;
    private PartnerAddress address;
    private double rating;
    private PartnerStatus status;
}


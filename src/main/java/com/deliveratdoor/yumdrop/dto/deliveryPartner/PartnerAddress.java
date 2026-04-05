package com.deliveratdoor.yumdrop.dto.deliveryPartner;

import lombok.Data;

@Data
public class PartnerAddress {
    private String language;
    private String city;
    private String region;
    private String postalCode;
    private String country;
}

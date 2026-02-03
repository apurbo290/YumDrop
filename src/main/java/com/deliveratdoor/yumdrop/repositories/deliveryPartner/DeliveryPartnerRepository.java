package com.deliveratdoor.yumdrop.repositories.deliveryPartner;

import com.deliveratdoor.yumdrop.entity.deliveryPartner.DeliveryPartnerEntity;
import com.deliveratdoor.yumdrop.model.PartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartnerEntity, Long> {

    List<DeliveryPartnerEntity> findByStatusAndActiveTrue(PartnerStatus status);
}


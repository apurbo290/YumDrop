package com.deliveratdoor.yumdrop.repositories.user;

import com.deliveratdoor.yumdrop.entity.user.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.userId = :userId")
    void deleteAllByUserId(Long userId);
}

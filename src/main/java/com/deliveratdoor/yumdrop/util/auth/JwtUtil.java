package com.deliveratdoor.yumdrop.util.auth;

import com.deliveratdoor.yumdrop.entity.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    @Getter
    private final String secret;
    private final long expiry;
    @Getter
    private final long refreshExpiry;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiry}") long expiry,
            @Value("${jwt.refresh-expiry}") long refreshExpiry
    ) {
        this.secret = secret;
        this.expiry = expiry;
        this.refreshExpiry = refreshExpiry;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserEntity user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getRole());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claims(extraClaims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public Instant refreshTokenExpiresAt() {
        return Instant.now().plusMillis(refreshExpiry);
    }

    public Long extractUserId(String token) {
        return Long.parseLong(
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }

    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return List.of(claims.get("roles", String.class));
    }
}



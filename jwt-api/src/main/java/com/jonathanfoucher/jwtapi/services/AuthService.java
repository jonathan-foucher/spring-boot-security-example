package com.jonathanfoucher.jwtapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jonathanfoucher.jwtapi.common.KeyUtils;
import com.jonathanfoucher.jwtapi.config.JwtConfig;
import com.jonathanfoucher.jwtapi.data.dto.Jwks;
import com.jonathanfoucher.jwtapi.data.dto.UserDto;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtConfig.class)
public class AuthService {
    private final JwtConfig jwtConfig;

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .propertyNamingStrategy(SNAKE_CASE)
                .build();
    }

    public String generateToken(UserDto user) {
        user.setUuid(UUID.randomUUID());
        Map<String, Object> claims = objectMapper.convertValue(user, new TypeReference<>() {
        });

        return Jwts.builder()
                .claims(claims)
                .header()
                .keyId(jwtConfig.getKeyId())
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationTime()))
                .signWith(KeyUtils.getPrivateKey())
                .compact();
    }

    public Jwks getJwks() {
        return KeyUtils.getJwks();
    }
}

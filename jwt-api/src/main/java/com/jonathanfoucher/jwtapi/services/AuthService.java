package com.jonathanfoucher.jwtapi.services;

import com.jonathanfoucher.jwtapi.common.KeyUtils;
import com.jonathanfoucher.jwtapi.config.JwtConfig;
import com.jonathanfoucher.jwtapi.data.dto.Jwks;
import com.jonathanfoucher.jwtapi.data.dto.UserDto;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtConfig.class)
public class AuthService {
    private final JwtConfig jwtConfig;

    private static final JsonMapper jsonMapper = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .build();

    public String generateToken(UserDto user) {
        user.setUuid(UUID.randomUUID());
        Map<String, Object> claims = jsonMapper.convertValue(user, new TypeReference<>() {
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

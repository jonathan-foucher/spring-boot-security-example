package com.jonathanfoucher.jwtapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jonathanfoucher.jwtapi.common.KeyUtils;
import com.jonathanfoucher.jwtapi.config.JwtConfig;
import com.jonathanfoucher.jwtapi.data.dto.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.RsaPublicJwk;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE;

@Service
@EnableConfigurationProperties(JwtConfig.class)
public class AuthService {
    private final JwtConfig jwtConfig;
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .propertyNamingStrategy(SNAKE_CASE)
                .build();
    }

    public AuthService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        try {
            privateKey = KeyUtils.readPrivateKey(jwtConfig.getPrivateKeyFilePath());
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load private key", exception);
        }

        try {
            publicKey = KeyUtils.readPublicKey(jwtConfig.getPublicKeyFilePath());
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load public key", exception);
        }
    }

    public String generateToken(UserDto user) {
        Map<String, Object> claims = objectMapper.convertValue(user, new TypeReference<>() {
        });

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationTime()))
                .signWith(privateKey)
                .compact();
    }

    public RsaPublicJwk getJwk() {
        return Jwks.builder()
                .key(publicKey)
                .build();
    }
}

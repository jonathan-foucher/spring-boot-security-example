package com.jonathanfoucher.jwtapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("jwt")
public class JwtConfig {
    private String publicKeyFilePath;
    private String privateKeyFilePath;
    private Integer expirationTime;
}

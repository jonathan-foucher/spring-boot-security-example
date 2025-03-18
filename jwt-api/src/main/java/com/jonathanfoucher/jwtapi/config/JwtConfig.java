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
    private String privateKeyFilePath;
    private String publicCertFilePath;
    private String keyId;
    private Integer expirationTime;
}

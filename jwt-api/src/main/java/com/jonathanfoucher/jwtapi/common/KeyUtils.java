package com.jonathanfoucher.jwtapi.common;

import com.jonathanfoucher.jwtapi.config.JwtConfig;
import com.jonathanfoucher.jwtapi.data.dto.Jwks;
import com.jonathanfoucher.jwtapi.data.dto.JwksKey;
import io.jsonwebtoken.io.Decoders;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;

@Component
@EnableConfigurationProperties(JwtConfig.class)
public class KeyUtils {
    private final JwtConfig jwtConfig;
    private static final String RSA_ALGORITHM = "RSA";

    @Getter
    private static PrivateKey privateKey;
    @Getter
    private static Jwks jwks;

    public KeyUtils(JwtConfig jwtConfig) throws Exception {
        this.jwtConfig = jwtConfig;
        if (privateKey == null) {
            privateKey = readPrivateKey(jwtConfig.getPrivateKeyFilePath());
        }
        if (jwks == null) {
            jwks = getJwks(jwtConfig.getPublicCertFilePath(), privateKey);
        }
    }

    private PrivateKey readPrivateKey(String filePath) throws Exception {
        String fileContent = readFile(filePath);

        String key = fileContent.replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] decoded = Decoders.BASE64.decode(key);

        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded, RSA_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    private Jwks getJwks(String publicCertPath, PrivateKey privateKey) throws Exception {
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                ((RSAPrivateCrtKey) privateKey).getModulus(),
                ((RSAPrivateCrtKey) privateKey).getPublicExponent()
        );

        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance(RSA_ALGORITHM)
                .generatePublic(publicKeySpec);

        JwksKey key = JwksKey.builder()
                .kty(RSA_ALGORITHM)
                .kid(jwtConfig.getKeyId())
                .use("sig")
                .alg("RS256")
                .n(Base64.getUrlEncoder().encodeToString(publicKey.getModulus().toByteArray()))
                .e(Base64.getUrlEncoder().encodeToString(publicKey.getPublicExponent().toByteArray()))
                .x5c(List.of(readPublicCertificateB64String(publicCertPath)))
                .build();

        return Jwks.builder()
                .keys(List.of(key))
                .build();
    }

    private String readPublicCertificateB64String(String filePath) throws Exception {
        String fileContent = readFile(filePath);

        return fileContent.replace("-----BEGIN CERTIFICATE-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END CERTIFICATE-----", "");
    }

    private String readFile(String filePath) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(filePath);
        return Files.readString(resource.getFile().toPath());
    }
}

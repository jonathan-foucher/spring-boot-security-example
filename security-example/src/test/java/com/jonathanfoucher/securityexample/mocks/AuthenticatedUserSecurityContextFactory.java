package com.jonathanfoucher.securityexample.mocks;

import com.jonathanfoucher.securityexample.data.dto.UserDto;
import com.jonathanfoucher.securityexample.security.AuthenticatedUserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.*;

public class AuthenticatedUserSecurityContextFactory implements WithSecurityContextFactory<MockedAuthentication> {
    @Override
    public SecurityContext createSecurityContext(MockedAuthentication mockedAuthentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uuid", mockedAuthentication.uuid());
        claims.put("first_name", mockedAuthentication.firstName());
        claims.put("last_name", mockedAuthentication.lastName());
        claims.put("job", mockedAuthentication.job());
        claims.put("roles", mockedAuthentication.roles());

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");

        Jwt jwt = new Jwt(
                mockedAuthentication.jwt(),
                Instant.parse(mockedAuthentication.issuedAt()),
                Instant.parse(mockedAuthentication.expiresAt()),
                headers,
                claims
        );

        UserDto user = new UserDto();
        user.setUuid(UUID.fromString(mockedAuthentication.uuid()));
        user.setFirstName(mockedAuthentication.firstName());
        user.setLastName(mockedAuthentication.lastName());
        user.setJob(mockedAuthentication.job());

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : mockedAuthentication.roles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new AuthenticatedUserDto(jwt, user, authorities));
        return context;
    }
}

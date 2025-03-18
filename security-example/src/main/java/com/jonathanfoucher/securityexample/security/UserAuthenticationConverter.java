package com.jonathanfoucher.securityexample.security;

import com.jonathanfoucher.securityexample.data.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class UserAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        UserDto user = extractUserInfo(jwt);
        List<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);
        return new AuthenticatedUserDto(jwt, user, authorities);
    }

    private UserDto extractUserInfo(Jwt jwt) {
        UserDto user = new UserDto();
        user.setUuid(UUID.fromString(jwt.getClaimAsString("uuid")));
        user.setFirstName(jwt.getClaimAsString("first_name"));
        user.setLastName(jwt.getClaimAsString("last_name"));
        user.setJob(jwt.getClaimAsString("job"));
        return user;
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> roles = (List<String>) jwt.getClaims().get("roles");

        if (isEmpty(roles)) {
            return emptyList();
        }

        return roles.stream()
                .map(String::toUpperCase)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

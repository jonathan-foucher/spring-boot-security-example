package com.jonathanfoucher.securityexample.security;

import com.jonathanfoucher.securityexample.data.dto.UserDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

@Getter
@Setter
public class AuthenticatedUserDto extends AbstractAuthenticationToken {
    private Jwt jwt;
    private UserDto user;

    public AuthenticatedUserDto(Jwt jwt, UserDto user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt;
        this.user = user;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}

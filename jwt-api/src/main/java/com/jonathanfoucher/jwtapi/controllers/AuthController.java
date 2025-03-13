package com.jonathanfoucher.jwtapi.controllers;

import com.jonathanfoucher.jwtapi.data.dto.UserDto;
import com.jonathanfoucher.jwtapi.services.AuthService;
import io.jsonwebtoken.security.RsaPublicJwk;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/authentication")
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public String generateToken(@RequestBody UserDto user) {
        return authService.generateToken(user);
    }

    @GetMapping("/jwk")
    public RsaPublicJwk getJwk() {
        return authService.getJwk();
    }
}

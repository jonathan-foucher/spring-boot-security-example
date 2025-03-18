package com.jonathanfoucher.jwtapi.controllers;

import com.jonathanfoucher.jwtapi.data.dto.Jwks;
import com.jonathanfoucher.jwtapi.data.dto.UserDto;
import com.jonathanfoucher.jwtapi.services.AuthService;
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

    @GetMapping("/jwks")
    public Jwks getJwks() {
        return authService.getJwks();
    }
}

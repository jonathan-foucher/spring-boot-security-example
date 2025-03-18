package com.jonathanfoucher.securityexample.controllers;

import com.jonathanfoucher.securityexample.data.dto.UserDto;
import com.jonathanfoucher.securityexample.services.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Slf4j
public class AuthController {
    @SuppressWarnings("unused")
    private final SecurityService securityService;

    @GetMapping("/unauthenticated")
    public void getUnauthenticated() {
        log.info("Unauthenticated request received");
    }

    @GetMapping("/authenticated")
    public void getAuthenticated() {
        log.info("Authenticated request received");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void getAdmin() {
        log.info("Admin request received");
    }

    @GetMapping("/authorized_job")
    @PreAuthorize("@securityService.isAuthorizedJob(authentication.principal)")
    public void getAuthorizedJob() {
        log.info("Authorized job request received");
    }

    @GetMapping("/uuid/{uuid}")
    @PreAuthorize("@securityService.isUuidEquals(authentication.principal, #uuid)")
    public void getAuthorizedUuid(@PathVariable UUID uuid) {
        log.info("Uuid {} matching requested uuid {}",
                ((UserDto) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal())
                        .getUuid(),
                uuid);
    }
}

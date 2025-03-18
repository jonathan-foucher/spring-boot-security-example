package com.jonathanfoucher.securityexample.services;

import com.jonathanfoucher.securityexample.data.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(SecurityService.class)
class SecurityServiceTest {
    @Autowired
    private SecurityService securityService;

    private static final UUID UUID_VALUE = UUID.fromString("d0418816-a0f2-4d78-822a-7618403be312");
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String JOB = "IT engineer";

    @Test
    void isAuthorizedJob() {
        // GIVEN
        UserDto user = initUser();
        user.setJob("developer");

        // WHEN
        boolean result = securityService.isAuthorizedJob(user);

        // THEN
        assertTrue(result);
    }

    @Test
    void isAuthorizedJobWithUnauthorizedJob() {
        // GIVEN
        UserDto user = initUser();

        // WHEN
        boolean result = securityService.isAuthorizedJob(user);

        // THEN
        assertFalse(result);
    }

    @Test
    void isUuidEquals() {
        // GIVEN
        UserDto user = initUser();

        // WHEN
        boolean result = securityService.isUuidEquals(user, UUID_VALUE);

        // THEN
        assertTrue(result);
    }

    @Test
    void isUuidEqualsWithWrongUuid() {
        // GIVEN
        UserDto user = initUser();

        // WHEN
        boolean result = securityService.isUuidEquals(user, UUID.fromString("9b38e5b2-4422-40ca-9e3b-64271c89c5a9"));

        // THEN
        assertFalse(result);
    }

    private UserDto initUser() {
        UserDto user = new UserDto();
        user.setUuid(UUID_VALUE);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setJob(JOB);
        return user;
    }
}

package com.jonathanfoucher.securityexample.services;

import com.jonathanfoucher.securityexample.data.dto.UserDto;
import com.jonathanfoucher.securityexample.data.enums.AuthorizedJob;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@NoArgsConstructor
public class SecurityService {
    public boolean isAuthorizedJob(UserDto user) {
        if (user.getJob() == null) {
            return false;
        }
        return AuthorizedJob.getAll().contains(user.getJob().toUpperCase());
    }

    public boolean isUuidEquals(UserDto user, @NonNull UUID uuid) {
        return uuid.equals(user.getUuid());
    }
}

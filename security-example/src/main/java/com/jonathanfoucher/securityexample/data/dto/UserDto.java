package com.jonathanfoucher.securityexample.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDto {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String job;
}

package com.jonathanfoucher.jwtapi.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDto {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String job;
    private List<String> roles = new ArrayList<>();
}

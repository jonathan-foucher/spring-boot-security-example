package com.jonathanfoucher.jwtapi.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto {
    private String firstName;
    private String lastName;
    private String job;
    private List<String> roles = new ArrayList<>();
}

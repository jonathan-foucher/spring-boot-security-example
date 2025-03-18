package com.jonathanfoucher.jwtapi.data.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Jwks {
    private List<JwksKey> keys;
}

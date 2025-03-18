package com.jonathanfoucher.jwtapi.data.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class JwksKey {
    private String kty;
    private String use;
    private String alg;
    private String kid;
    private String n;
    private String e;
    private List<String> x5c;
}

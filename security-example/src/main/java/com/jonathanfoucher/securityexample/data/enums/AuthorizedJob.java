package com.jonathanfoucher.securityexample.data.enums;

import java.util.Arrays;
import java.util.List;

public enum AuthorizedJob {
    DEVELOPER,
    DEVOPS;

    public static List<String> getAll() {
        return Arrays.stream(AuthorizedJob.values())
                .map(AuthorizedJob::name)
                .toList();
    }
}

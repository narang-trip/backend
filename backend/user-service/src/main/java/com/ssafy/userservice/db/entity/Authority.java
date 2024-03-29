package com.ssafy.userservice.db.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Authority {

    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String key;
}


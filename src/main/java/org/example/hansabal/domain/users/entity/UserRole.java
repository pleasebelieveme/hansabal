package org.example.hansabal.domain.users.entity;

import java.util.Arrays;

public enum UserRole {

    USER, ADMIN;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("유효하지 않은 UerRole"));
    }
}

package com.kimhyunsu.todolist.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum Authority {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String role_user;

    Authority(String role_user) {
        this.role_user = role_user;
    }

    public String getKey() {
        return name();
    }

    public String getValue() {
        return role_user;
    }
}

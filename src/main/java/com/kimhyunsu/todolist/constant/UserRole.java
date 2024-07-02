package com.kimhyunsu.todolist.constant;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String userRole;

    UserRole(String role_user) {
        this.userRole = role_user;
    }

}

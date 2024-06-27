package com.kimhyunsu.todolist.constant;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("일반사용자"),
    ADMIN("관리자");

    private final String userRole;

    UserRole(String role_user) {
        this.userRole = role_user;
    }

}

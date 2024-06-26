package com.kimhyunsu.todolist.dto;

import lombok.Data;

@Data
public class MemberLoginRequest {
    private String email;
    private String password;
}

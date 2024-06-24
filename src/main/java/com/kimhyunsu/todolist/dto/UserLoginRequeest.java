package com.kimhyunsu.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginRequeest {
    private String userName;
    private String password;
}

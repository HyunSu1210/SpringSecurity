package com.kimhyunsu.todolist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 기존 자바에서 제공하는 exception 뿐만 아니라, 사용자가 custom 해서 사용할 수 있는 custom exception.
@AllArgsConstructor
@Getter
public class AppException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;
}

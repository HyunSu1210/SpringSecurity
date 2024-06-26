package com.kimhyunsu.todolist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/all")
    public ResponseEntity<String> adminAll(Authentication authentication) {
        return ResponseEntity.ok().body(authentication.getName() + "님의 admin 접근 성공");
    }
}

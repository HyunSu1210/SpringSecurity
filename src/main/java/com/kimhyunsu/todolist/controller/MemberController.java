package com.kimhyunsu.todolist.controller;

import com.kimhyunsu.todolist.dto.MemberJoinRequest;
import com.kimhyunsu.todolist.dto.MemberLoginRequest;
import com.kimhyunsu.todolist.dto.TokenDto;
import com.kimhyunsu.todolist.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody MemberJoinRequest dto) {
        memberService.join(dto);
        return ResponseEntity.ok().body("회원가입 성공");
    }

    @PostMapping("/login")
    public TokenDto login(@RequestBody MemberLoginRequest memberLoginRequest) {
        TokenDto tokenDto = memberService.login(memberLoginRequest);
        return tokenDto;
    }
}

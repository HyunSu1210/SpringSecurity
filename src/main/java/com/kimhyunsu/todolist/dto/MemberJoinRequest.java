package com.kimhyunsu.todolist.dto;

import com.kimhyunsu.todolist.constant.UserRole;
import com.kimhyunsu.todolist.entity.Member;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class MemberJoinRequest {
    private String email;
    private String userName;
    private String password;
    private List<String> roles;

    public Member toEntity(PasswordEncoder passwordEncoder) { // 회원가입 시 사용
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .userName(userName)
                .userRole(UserRole.USER)
                .build();
    }
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}

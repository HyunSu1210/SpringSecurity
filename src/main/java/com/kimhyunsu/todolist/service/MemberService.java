package com.kimhyunsu.todolist.service;

import com.kimhyunsu.todolist.config.EncoderConfig;
import com.kimhyunsu.todolist.dto.MemberJoinRequest;
import com.kimhyunsu.todolist.dto.MemberLoginRequest;
import com.kimhyunsu.todolist.dto.TokenDto;
import com.kimhyunsu.todolist.entity.Member;
import com.kimhyunsu.todolist.repository.MemberRepository;
import com.kimhyunsu.todolist.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenUtil jwtTokenUtil;

    /*
     * 1. 로그인 요청으로 들어온 ID, PWD 기반으로 Authentication 객체 생성
     * 2. authenticate() 메서드를 통해 요청된 Member에 대한 검증이 진행 => loadUserByUsername 메서드를 실행. 해당 메서드는 검증을 위한 유저 객체를 가져오는 부분으로써, 어떤 객체를 검증할 것인지에 대해 직접 구현
     * 3. 검증이 정상적으로 통과되었다면 인증된 Authentication객체를 기반으로 JWT 토큰을 생성
     */

    // 회원가입
    @Transactional
    public boolean join(MemberJoinRequest memberJoinRequest) {
        if (memberRepository.existsByEmail(memberJoinRequest.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }
        Member member = memberJoinRequest.toEntity(EncoderConfig.encoder());
        memberRepository.save(member);
        return true;
    }

    @Transactional
    public TokenDto login(MemberLoginRequest memberLoginRequest) {
        // 1. login 정보를 기반으로 Authentication 객체 생성
        // 이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginRequest.getEmail(), memberLoginRequest.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크) 이루어지는 부분
        // authenticate 메서드가 실행될 때 CustomUserDetailsService에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = JwtTokenUtil.generateToken(authentication);
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        return tokenDto;
    }

    // accessToken 재발급
    public String createAccessToken(String refreshToken) {
        Authentication authentication = JwtTokenUtil.getAuthentication(refreshToken);
        return JwtTokenUtil.generateAccessToken(authentication);
    }
}

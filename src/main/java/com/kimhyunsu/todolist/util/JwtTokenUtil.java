package com.kimhyunsu.todolist.util;

import com.kimhyunsu.todolist.dto.TokenDto;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenUtil {

    private static long accessTokenExpireTimeMs = 1000 * 60 * 60L; // 1시간
    private static long refreshTokenExpireTimeMs = 1000 * 60 * 60 * 24 * 7L; // 7일

    private static Key key;
    public JwtTokenUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 안의 userName 파싱
    public static String getUserName(String token, String secretKey) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
                .getBody().get("userName", String.class);
    }

    // 존재하는 토큰인지 검증
    public static boolean isExpired(String token, String secretKey) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }

    // 유저의 role 검색
    public static String getAuthorities(String token, String secretKey) {
        String str = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
                .getBody().get("roles", String.class);
        log.warn("얻은 role? {}", str);
        return str;
    }


    // 토큰 생성
    public static TokenDto generateToken(Authentication authentication) {
        log.warn("generateToken 메서드 진입");

        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 액세스 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // 주체 : 현재 인증된 사용자의 이름
                .claim("auth", authorities)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 만든 날짜
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpireTimeMs)) // 만료 날짜
                .signWith(key) // HS256 알고리즘으로 sign
                .compact();

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis())) // 만든 날짜
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpireTimeMs)) // 만료 날짜
                .signWith(key) // HS256 알고리즘으로 sign
                .compact();

        return TokenDto.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public static Authentication getAuthentication(String refreshToken) {
        // 토큰 복호화
        Claims claims = parseClaims(refreshToken);

        // 토큰 복호화에 실패하면
        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에 담긴 권한 정보 가져옴
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 권한 정보들을 이용해 유저 객체를 만들어서 반환, 여기서 User 객체는 UserDetails 인터페이스를 구현한 객체
        User principal = new User(claims.getSubject(), "", authorities);

        // 유저 객체, 토큰, 권한 정보들을 이용해 인증 객체를 생성해서 반환
        return new UsernamePasswordAuthenticationToken(principal, refreshToken, authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // 토큰 복호화
    private static Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // access 토큰 재발급
    public static String generateAccessToken(Authentication authentication) {
        return generateToken(authentication).getAccessToken();
    }
}

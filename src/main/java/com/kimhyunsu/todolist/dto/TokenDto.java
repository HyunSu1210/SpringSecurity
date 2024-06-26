package com.kimhyunsu.todolist.dto;

import lombok.*;

/*
 * 클라이언트에 토큰을 보내기 위한 DTO
 * grantType: JWT에 대한 인증 타입. 여기서는 Bearer를 사용한다. 이후 HHTP 헤더에 prefix로 붙여주는 타입
 */

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TokenDto {
    private String grantType; // 권한
    private String accessToken; // 액세스 토큰
    private String refreshToken; // 리프레시 토큰
}

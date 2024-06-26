package com.kimhyunsu.todolist.config;

import com.kimhyunsu.todolist.jwt.JwtAccessDeniedHandler;
import com.kimhyunsu.todolist.jwt.JwtAuthenticationEntryPoint;
import com.kimhyunsu.todolist.jwt.JwtAuthenticationFilter;
import com.kimhyunsu.todolist.service.CustomUserDetailsService;
import com.kimhyunsu.todolist.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/* SecurityConfig는 Spring Security 설정을 위한 클래스 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Component
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.secret}")
    private String secretKey;

    @Bean // SecurityFilterChain 객체를 Bean으로 등록
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionConfig ->
                        exceptionConfig
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/members/**").permitAll()
                        .requestMatchers("/review/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
                .build();


    }
   // @Override  // 메소드 오버라이딩, localhost:3000 번으로 들어오는 요청 허가
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

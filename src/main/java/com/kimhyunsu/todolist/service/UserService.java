package com.kimhyunsu.todolist.service;

import com.kimhyunsu.todolist.entity.User;
import com.kimhyunsu.todolist.exception.AppException;
import com.kimhyunsu.todolist.exception.ErrorCode;
import com.kimhyunsu.todolist.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public String join(String userName, String password) {

        // userName 중복 체크
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "은 이미 있습니다.");
                });

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }

    public String login(String userName, String password) {
        // userName 없음
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        // password 틀림
        if(!passwordEncoder.matches(user.getPassword(), password)) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력했습니다.");
        }

        return "SUCCESS";
    }
}

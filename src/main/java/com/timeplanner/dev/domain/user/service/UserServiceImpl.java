package com.timeplanner.dev.domain.user.service;

import com.timeplanner.dev.domain.user.entity.User;
import com.timeplanner.dev.domain.user.repository.UserRepository;
import com.timeplanner.dev.global.exception.ApiException;
import com.timeplanner.dev.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.builder()
                        .errorCode(ErrorCode.NOT_FOUND_USER)
                        .build());
    }
}

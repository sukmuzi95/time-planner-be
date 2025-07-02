package com.timeplanner.dev.domain.user.service;

import com.timeplanner.dev.domain.user.dto.UpdatePasswordRequest;
import com.timeplanner.dev.domain.user.dto.UserResponse;
import com.timeplanner.dev.domain.user.entity.User;
import com.timeplanner.dev.domain.user.repository.UserRepository;
import com.timeplanner.dev.global.exception.ApiException;
import com.timeplanner.dev.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.builder()
                        .errorCode(ErrorCode.NOT_FOUND_USER)
                        .build());
    }

    @Override
    @Transactional
    public void updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.builder()
                        .errorCode(ErrorCode.NOT_FOUND_USER)
                        .build());
        user.updateNickname(nickname);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.builder()
                        .errorCode(ErrorCode.NOT_FOUND_USER)
                        .build());

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw ApiException.builder()
                    .errorCode(ErrorCode.INVALID_INPUT_VALUE)
                    .build();
        }

        String encodedNewPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedNewPassword);
    }

    @Override
    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.builder()
                        .errorCode(ErrorCode.NOT_FOUND_USER)
                        .build());

        return UserResponse.of(user);
    }
}

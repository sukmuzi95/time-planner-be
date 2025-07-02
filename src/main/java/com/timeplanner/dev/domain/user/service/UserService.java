package com.timeplanner.dev.domain.user.service;

import com.timeplanner.dev.domain.user.dto.UpdatePasswordRequest;
import com.timeplanner.dev.domain.user.dto.UserResponse;
import com.timeplanner.dev.domain.user.entity.User;

public interface UserService {

    User getUser(String email);
    void updateNickname(Long userId, String nickname);

    void updatePassword(Long userId, UpdatePasswordRequest request);

    UserResponse findById(Long userId);
}

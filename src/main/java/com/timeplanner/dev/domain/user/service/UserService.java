package com.timeplanner.dev.domain.user.service;

import com.timeplanner.dev.domain.user.dto.request.UpdatePasswordRequest;
import com.timeplanner.dev.domain.user.dto.response.UserResponse;
import com.timeplanner.dev.domain.user.entity.User;

public interface UserService {

    User getUser(String email);
    void updateNickname(Long userId, String nickname);

    void updatePassword(Long userId, UpdatePasswordRequest request);

    UserResponse findById(Long userId);
}

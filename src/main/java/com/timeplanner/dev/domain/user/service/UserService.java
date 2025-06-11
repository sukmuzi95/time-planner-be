package com.timeplanner.dev.domain.user.service;

import com.timeplanner.dev.domain.user.entity.User;

public interface UserService {

    User getUser(String email);
}

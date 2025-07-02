package com.timeplanner.dev.domain.user.dto.request;

public record UserLoginRequest(
        String email,
        String password
) {}

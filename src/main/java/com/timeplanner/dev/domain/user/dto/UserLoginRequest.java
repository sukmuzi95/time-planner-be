package com.timeplanner.dev.domain.user.dto;

public record UserLoginRequest(
        String email,
        String password
) {}

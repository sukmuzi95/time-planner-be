package com.timeplanner.dev.domain.auth.dto;

public record EmailVerifyRequest(
        String email,
        String code
) {}

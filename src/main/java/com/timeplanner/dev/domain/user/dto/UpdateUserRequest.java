package com.timeplanner.dev.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String nickname
) {}

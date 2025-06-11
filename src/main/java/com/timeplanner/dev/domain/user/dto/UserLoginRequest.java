package com.timeplanner.dev.domain.user.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginRequest {
    private String email;
    private String password;
}

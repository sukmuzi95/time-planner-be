package com.timeplanner.dev.domain.user.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginResponse {
    private UserResponse user;
    private String accessToken;
}

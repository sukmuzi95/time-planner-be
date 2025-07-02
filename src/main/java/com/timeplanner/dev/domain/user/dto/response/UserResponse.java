package com.timeplanner.dev.domain.user.dto.response;

import com.timeplanner.dev.domain.user.entity.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}

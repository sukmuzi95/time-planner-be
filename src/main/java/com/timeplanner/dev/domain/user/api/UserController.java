package com.timeplanner.dev.domain.user.api;

import com.timeplanner.dev.domain.user.dto.UpdatePasswordRequest;
import com.timeplanner.dev.domain.user.dto.UpdateUserRequest;
import com.timeplanner.dev.domain.user.service.UserService;
import com.timeplanner.dev.global.security.auth.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
        @RequestBody @Valid UpdateUserRequest request,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userService.updateNickname(userDetails.getUser().getId(), request.nickname());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
        @RequestBody @Valid UpdatePasswordRequest request,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userService.updatePassword(userDetails.getUser().getId(), request);

        return ResponseEntity.ok().build();
    }
}

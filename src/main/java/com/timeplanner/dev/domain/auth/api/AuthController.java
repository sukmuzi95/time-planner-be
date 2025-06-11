package com.timeplanner.dev.domain.auth.api;

import com.timeplanner.dev.domain.user.dto.UserLoginRequest;
import com.timeplanner.dev.domain.user.dto.UserLoginResponse;
import com.timeplanner.dev.domain.auth.service.AuthService;
import com.timeplanner.dev.global.security.jwt.dto.JwtResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        UserLoginResponse userLoginResponse = authService.login(request, response);

        return ResponseEntity.ok().body(userLoginResponse);
    }

    @PostMapping("/reissue-token")
    public ResponseEntity<JwtResponse> reissueToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        log.info("reissueToken() refreshToken: {}", refreshToken);
        JwtResponse jwtResponse = authService.reissueToken(refreshToken);

        return ResponseEntity.ok().body(jwtResponse);
    }
}

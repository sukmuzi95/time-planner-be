package com.timeplanner.dev.domain.auth.api;

import com.timeplanner.dev.domain.auth.dto.EmailVerifyRequest;
import com.timeplanner.dev.domain.user.dto.request.UserLoginRequest;
import com.timeplanner.dev.domain.user.dto.response.UserLoginResponse;
import com.timeplanner.dev.domain.auth.service.AuthService;
import com.timeplanner.dev.domain.user.entity.User;
import com.timeplanner.dev.domain.user.repository.UserRepository;
import com.timeplanner.dev.global.security.jwt.dto.JwtResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        UserLoginResponse userLoginResponse = authService.login(request, response);

        return ResponseEntity.ok().body(userLoginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserLoginRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일이에요.");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        user.verifyEmail();

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue-token")
    public ResponseEntity<JwtResponse> reissueToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        log.info("reissueToken() refreshToken: {}", refreshToken);
        JwtResponse jwtResponse = authService.reissueToken(refreshToken);

        return ResponseEntity.ok().body(jwtResponse);
    }

    @PostMapping("/email/send")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody EmailVerifyRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일이에요.");
        }

        authService.sendVerificationCode(request.email());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Void> verifyCode(@RequestBody EmailVerifyRequest request) {
        if (!authService.verifyCode(request.email(), request.code())) {
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다.");
        }

        return ResponseEntity.ok().build();
    }
}

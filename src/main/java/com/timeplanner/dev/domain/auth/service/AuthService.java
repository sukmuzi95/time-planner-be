package com.timeplanner.dev.domain.auth.service;

import com.timeplanner.dev.domain.user.dto.request.UserLoginRequest;
import com.timeplanner.dev.domain.user.dto.response.UserLoginResponse;
import com.timeplanner.dev.global.security.jwt.dto.JwtResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    UserLoginResponse login(UserLoginRequest request, HttpServletResponse response);

    JwtResponse reissueToken(String requestRefreshToken);

    void sendVerificationCode(String email);

    boolean verifyCode(String email, String code);
}

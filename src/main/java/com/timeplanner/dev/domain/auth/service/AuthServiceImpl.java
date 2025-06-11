package com.timeplanner.dev.domain.auth.service;

import com.timeplanner.dev.domain.user.dto.UserLoginRequest;
import com.timeplanner.dev.domain.user.dto.UserLoginResponse;
import com.timeplanner.dev.domain.user.dto.UserResponse;
import com.timeplanner.dev.domain.user.service.UserService;
import com.timeplanner.dev.global.exception.ApiException;
import com.timeplanner.dev.global.exception.ErrorCode;
import com.timeplanner.dev.global.security.jwt.JwtTokenProvider;
import com.timeplanner.dev.global.security.jwt.dto.JwtResponse;
import com.timeplanner.dev.global.security.jwt.enumerated.TokenType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    @Transactional
    public UserLoginResponse login(UserLoginRequest request, HttpServletResponse response) {
        try {
            log.debug("AuthServiceImpl login() request email: {}, password: {}", request.getEmail(), request.getPassword());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            JwtResponse jwtResponse = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            ResponseCookie refreshCookie = jwtTokenProvider.setRefreshTokenInCookie(refreshToken);

            response.addHeader("Set-Cookie", refreshCookie.toString());

            UserResponse userResponse = UserResponse.of(userService.getUser(request.getEmail()));

            return UserLoginResponse.builder()
                    .user(userResponse)
                    .accessToken(jwtResponse.getAccessToken())
                    .build();
        } catch (Exception e) {
            log.error("Error occurred during authenticate. ", e);
            throw e;
        }
    }

    @Override
    public JwtResponse reissueToken(String requestRefreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(requestRefreshToken))
            throw new ApiException(ErrorCode.WRONG_TOKEN);

        Authentication authentication = jwtTokenProvider.getAuthentication(requestRefreshToken, TokenType.REFRESH_TOKEN.getValue());

        return jwtTokenProvider.generateAccessToken(authentication);
    }
}

package com.timeplanner.dev.domain.auth.service;

import com.timeplanner.dev.domain.auth.util.VerificationCodeRedisUtil;
import com.timeplanner.dev.domain.user.dto.request.UserLoginRequest;
import com.timeplanner.dev.domain.user.dto.response.UserLoginResponse;
import com.timeplanner.dev.domain.user.dto.response.UserResponse;
import com.timeplanner.dev.domain.user.service.UserService;
import com.timeplanner.dev.global.exception.ApiException;
import com.timeplanner.dev.global.exception.ErrorCode;
import com.timeplanner.dev.global.security.jwt.JwtTokenProvider;
import com.timeplanner.dev.global.security.jwt.dto.JwtResponse;
import com.timeplanner.dev.global.security.jwt.enumerated.TokenType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final VerificationCodeRedisUtil redisUtil;

    @Override
    @Transactional
    public UserLoginResponse login(UserLoginRequest request, HttpServletResponse response) {
        try {
            log.debug("AuthServiceImpl login() request email: {}, password: {}", request.email(), request.password());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            JwtResponse jwtResponse = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            ResponseCookie refreshCookie = jwtTokenProvider.setRefreshTokenInCookie(refreshToken);

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            UserResponse userResponse = UserResponse.of(userService.getUser(request.email()));

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

    @Override
    public void sendVerificationCode(String email) {
        if (redisUtil.cooldownCheck(email)) {
            throw new IllegalStateException("잠시 후 다시 시도해주세요."); // or 429 TOO MANY REQUESTS
        }

        try {
            String code = generateCode();
            redisUtil.save(email, code);

            Context context = new Context();
            context.setVariable("code", code);

            String html = templateEngine.process("email-verification", context);
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[TimePlanner] 이메일 인증 코드");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyCode(String email, String code) {
        return redisUtil.verifyCode(email, code);
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

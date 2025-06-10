package com.timeplanner.dev.global.security.jwt;

import com.timeplanner.dev.global.security.auth.UserDetailsImpl;
import com.timeplanner.dev.global.security.auth.UserDetailsServiceImpl;
import com.timeplanner.dev.global.security.jwt.dto.JwtResponse;
import com.timeplanner.dev.global.security.jwt.enumerated.TokenType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.access-token-secret-key}")
    private String accessTokenSecretKey;

    @Value("${jwt.refresh-token-secret-key}")
    private String refreshTokenSecretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final UserDetailsServiceImpl userDetailsService;

    public JwtResponse generateAccessToken(Authentication authentication) {
        String accessToken = this.createAccessToken(authentication, accessTokenExpiration);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public String generateRefreshToken(Authentication authentication) {
//        Date expiryDate = this.createExpiryDate(accessTokenExpiration);

        return this.createRefreshToken(authentication, refreshTokenExpiration);
    }

    public String createAccessToken(Authentication authentication, long tokenExpiryDate) {
        return this.createToken(authentication, tokenExpiryDate, accessTokenSecretKey);
    }

    public String createRefreshToken(Authentication authentication, long tokenExpiryDate) {
        //        Date expiryDate = this.createExpiryDate(refreshTokenExpiration);
//        this.tokenService.createRefreshToken(token, expiryDate, authentication);

        return this.createToken(authentication, tokenExpiryDate, refreshTokenSecretKey);
    }

    public String createToken(Authentication authentication, long tokenExpiryDate, String secretKey) {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .claim("auth", role)
                .subject(authentication.getName())
                .issuedAt(new Date())
                .expiration(createExpiryDate(tokenExpiryDate))
                .signWith(createKey(secretKey))
                .compact();
    }

    public Date createExpiryDate(long tokenExpiryDate) {
        return new Date(new Date().getTime() + tokenExpiryDate);
    }

    public SecretKey createKey(String tokenSecret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
    }

    public String getMidFromToken(String token, String type) {
        String secretKey = type.equals(TokenType.ACCESS_TOKEN.getValue()) ? accessTokenSecretKey : refreshTokenSecretKey;

        return Jwts.parser()
                .verifyWith(createKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Authentication getAuthentication(String token, String type) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(getMidFromToken(token, type));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().verifyWith(createKey(accessTokenSecretKey)).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            log.error("토큰 검증 실패", e);
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().verifyWith(createKey(refreshTokenSecretKey)).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            log.error("토큰 검증 실패", e);
            return false;
        }
    }

    public ResponseCookie setRefreshTokenInCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/v1/auth/reissue-token")
                .maxAge(refreshTokenExpiration / 1000) // 7일
                .build();
    }
}

package com.ssafy.enjoytrip.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    public static final String REQUEST_ATTRIBUTE_NAME = "loginUserId";
    public static final long ACCESS_TOKEN_EXPIRATION_TIME_MILLIS = 1000L * 60 * 30;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME_MILLIS = 1000L * 60 * 60 * 24 * 14;

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private static final String SECRET_KEY =
            "enjoytrip-local-development-secret-key-must-be-at-least-32-bytes";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // AT 생성
    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRATION_TIME_MILLIS, ACCESS_TOKEN_TYPE);
    }

    // RT 생성
    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH_TOKEN_EXPIRATION_TIME_MILLIS, REFRESH_TOKEN_TYPE);
    }

    // 토큰 생성 (AT or RT)
    private String createToken(Long userId, long expirationTimeMillis, String tokenType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTimeMillis);

        return Jwts.builder()
        		.subject(String.valueOf(userId))
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return ACCESS_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // RT 유효성 검사 -> AT 재발급 가능 여부 판별
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 -> 유저 ID로 파싱
    public Long getUserId(String token) {
        try {
        	return Long.valueOf(parseClaims(token).getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}


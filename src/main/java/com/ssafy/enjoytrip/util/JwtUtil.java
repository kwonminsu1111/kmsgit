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

    public static final String COOKIE_NAME = "accessToken";
    public static final String REQUEST_ATTRIBUTE_NAME = "loginUserId";
    public static final long EXPIRATION_TIME_MILLIS = 1000L * 60 * 60;

    private static final String SECRET_KEY =
            "enjoytrip-local-development-secret-key-must-be-at-least-32-bytes";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String createToken(String userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME_MILLIS);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        try {
            return parseClaims(token).getSubject();
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


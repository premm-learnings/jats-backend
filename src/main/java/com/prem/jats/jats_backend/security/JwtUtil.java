package com.prem.jats.jats_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 1️⃣ Secret key (should be strong)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 2️⃣ Token validity (30 minutes)
    private final long jwtExpirationMs = 30 * 60 * 1000;

    // 3️⃣ Generate JWT
    public String generateToken(Long userId, String email) {

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    // 4️⃣ Extract email from token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 5️⃣ Extract userId from token
    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    // 6️⃣ Validate token
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 7️⃣ Internal method to parse token
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

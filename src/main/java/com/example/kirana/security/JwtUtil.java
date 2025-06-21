package com.example.kirana.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY; // Use env variable in production

    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1)) // 1 minutes
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUserId(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody().getSubject();
    }
}
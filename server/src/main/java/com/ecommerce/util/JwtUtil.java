package com.ecommerce.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY =
        "THIS_IS_A_VERY_LONG_SECRET_KEY_FOR_ECOMMERCE_APP_2025_JWT";

    private static final Key key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 giờ

    public static String generateToken(String userId, String role) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    
    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    public static String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

}

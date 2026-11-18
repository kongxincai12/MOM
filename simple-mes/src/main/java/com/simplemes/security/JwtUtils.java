package com.simplemes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具，沿用主项目实现：HS256 + Base64 密钥。
 */
@Component
public class JwtUtils {

    private Key secretKey;

    @Value("${jwt.secret}")
    private String secretString;

    /** Token 有效期：24 小时 */
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String subject, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }
}

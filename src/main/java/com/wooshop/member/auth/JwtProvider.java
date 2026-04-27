package com.wooshop.member.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    // 토큰 생성 (email을 subject로)
    public String generateToken(String email) {
        // Jwts.builder() 사용
        // subject: email
        // issuedAt: 현재 시간
        // expiration: 현재 시간 + expiration
        // signWith: secretKey
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiryDate = new Date(nowMillis + expiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 email 추출
    public String getEmail(String token) {
        // Jwts.parser() 사용
        // parseSignedClaims(token)
        // .getPayload().getSubject()
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        // getEmail() 호출해서 예외 없이 통과하면 true
        // 예외 발생 시 false
        try {
            getEmail(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

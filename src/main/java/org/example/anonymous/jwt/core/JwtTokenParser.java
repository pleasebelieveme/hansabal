package org.example.anonymous.jwt.core;

import java.util.Date;

import javax.crypto.SecretKey;

import org.example.anonymous.domain.auth.dto.UserAuth;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtTokenParser {

    private final SecretKey secretKey;
    private final ObjectMapper objectMapper;

    private Claims parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException expiredJwtException) {
            return expiredJwtException.getClaims();
        } catch (MalformedJwtException malformedJwtException) {
            return (Claims)malformedJwtException;
        } catch (SignatureException signatureException) {
            return (Claims)signatureException;
        } catch (UnsupportedJwtException unsupportedJwtException) {
            return (Claims)unsupportedJwtException;
        }
    }

    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }

    public UserAuth getUserAuth(String token) {
        Claims claims = parseToken(token);

        return UserAuth.builder()
                .id(Long.valueOf(claims.getId()))
                .email(claims.getSubject())
                .build();
    }

    public Date getTokenExpiration(String token) {
        Claims claims = parseToken(token);

        return claims.getExpiration();
    }
}

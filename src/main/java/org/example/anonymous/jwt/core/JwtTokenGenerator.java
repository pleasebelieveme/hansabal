package org.example.anonymous.jwt.core;

import java.util.Date;

import javax.crypto.SecretKey;

import org.example.anonymous.domain.auth.dto.UserAuth;
import org.example.anonymous.jwt.constants.JwtConstants;
import org.example.anonymous.jwt.constants.TokenExpiredConstants;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final SecretKey secretKey;
    private final TokenExpiredConstants tokenExpiredConstant;

    public String generateAccessToken(UserAuth userAuth, Date date) {
        return buildToken(userAuth, JwtConstants.ACCESS_TOKEN, date,
                tokenExpiredConstant.getAccessTokenExpirationTime(date));
    }

    public String generateRefreshToken(UserAuth userAuth, Date date) {
        return buildToken(userAuth, JwtConstants.REFRESH_TOKEN, date,
                tokenExpiredConstant.getRefreshTokenExpirationTime(date));
    }

    private String buildToken(UserAuth userAuth, String tokenType, Date date, Date TokenExpiredTime) {
        return Jwts.builder()
                .setSubject(userAuth.getEmail())
                .setId(userAuth.getId().toString())
                .claim(JwtConstants.TOKEN_TYPE, tokenType)
                .setExpiration(TokenExpiredTime)
                .setIssuedAt(date)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}

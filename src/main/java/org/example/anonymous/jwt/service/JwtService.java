package org.example.anonymous.jwt.service;

import java.util.Date;

import org.example.anonymous.domain.auth.dto.UserAuth;
import org.example.anonymous.domain.auth.dto.response.TokenResponse;
import org.example.anonymous.jwt.constants.TokenExpiredConstants;
import org.example.anonymous.jwt.core.JwtTokenGenerator;
import org.example.anonymous.jwt.core.JwtTokenParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenParser jwtParser;

    public JwtService(@Value("${spring.jwt.secret}") String secretKey, TokenExpiredConstants tokenExpiredConstant) {
        this.jwtTokenGenerator = new JwtTokenGenerator(Keys.hmacShaKeyFor(secretKey.getBytes()), tokenExpiredConstant);
        this.jwtParser = new JwtTokenParser(Keys.hmacShaKeyFor(secretKey.getBytes()), new ObjectMapper());
    }

    public TokenResponse generateToken(UserAuth userAuth, Date date) {

        String accessToken = jwtTokenGenerator.generateAccessToken(userAuth, date);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(userAuth, date);

        return TokenResponse.of(accessToken, refreshToken);
    }

    public void addBlackListToken(String accessToken) {

        Date tokenExpiredTime = jwtParser.getTokenExpiration(accessToken);
        long now = System.currentTimeMillis();
        long ttl = (tokenExpiredTime.getTime() - now) / 1000L;

        if (ttl <= 0){
            return;
        }
    }

    public TokenResponse reissueToken(UserAuth userAuth, String refreshToken) throws Exception {
        if (jwtParser.isTokenExpired(refreshToken)) {
            throw new Exception("토큰이 만료되었습니다.");
        }

        return TokenResponse.builder()
                .accessToken(jwtTokenGenerator.generateAccessToken(userAuth, new Date()))
                .build();
    }

    public boolean isTokenExpired(String token) {
        return jwtParser.isTokenExpired(token);
    }

    public UserAuth getUserAuth(String token) {
        return jwtParser.getUserAuth(token);
    }

}

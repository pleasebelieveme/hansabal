package org.example.anonymous.jwt.constants;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenExpiredConstants {

    public static final long MILLISECOND = 1000L;

    @Value("${spring.jwt.token.access.second}")
    private long accessSecond;

    @Value("${spring.jwt.token.access.minute}")
    private long accessMinute;

    @Value("${spring.jwt.token.access.hour}")
    private long accessHour;

    @Value("${spring.jwt.token.refresh.second}")
    private long refreshSecond;

    @Value("${spring.jwt.token.refresh.minute}")
    private long refreshMinute;

    @Value("${spring.jwt.token.refresh.hour}")
    private long refreshHour;

    public long getAccessTokenExpirationSeconds() {
        long totalSeconds = (accessHour * 3600) + (accessMinute * 60) + accessSecond;
        return totalSeconds * MILLISECOND;
    }

    public long getRefreshTokenExpirationSeconds() {
        long totalSeconds = (refreshHour * 3600) + (refreshMinute * 60) + refreshSecond;
        return totalSeconds * MILLISECOND;
    }


    public Date getAccessTokenExpirationTime(Date date) {
        return new Date(date.getTime() + getAccessTokenExpirationSeconds());
    }

    public Date getRefreshTokenExpirationTime(Date date) {
        return new Date(date.getTime() + getRefreshTokenExpirationSeconds());
    }
}

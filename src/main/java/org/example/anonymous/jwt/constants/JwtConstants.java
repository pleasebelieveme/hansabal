package org.example.anonymous.jwt.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtConstants {
    public static final String AUTH_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh-Token";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String TOKEN_TYPE = "tokenType";
}

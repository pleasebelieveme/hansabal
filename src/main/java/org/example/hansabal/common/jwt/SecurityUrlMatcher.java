package org.example.hansabal.common.jwt;

import java.util.Arrays;

public class SecurityUrlMatcher {
	public static final String[] PUBLIC_URLS = {
		"/api-docs",
		"/swagger-ui.html",
		"/swagger-ui/**",
		"/api/users",
		"/api/auth/login",
		"/crawl",
		"/home",
		"/api/shop/search/**"
	};

	public static final String[] ADMIN_URLS = {
		"/api/admin/**"
	};

	public static final String REFRESH_URL = "/api/auth/reissue";

	public static boolean isRefreshUrl(String path) {
		return REFRESH_URL.equals(path);
	}

	public static boolean isPublicUrl(String path) {
		return Arrays.stream(PUBLIC_URLS).anyMatch(path::startsWith);
	}
}

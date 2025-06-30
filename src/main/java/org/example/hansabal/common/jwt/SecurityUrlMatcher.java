package org.example.hansabal.common.jwt;

import java.util.Arrays;

public class SecurityUrlMatcher {
	public static final String[] PUBLIC_URLS = {
		// Swagger/OpenAPI
		"/api-docs",
		"/v3/api-docs",
		"/v3/api-docs/**",
		"/swagger-ui.html",
		"/swagger-ui/**",
		"/swagger-resources/**",

		// 인증 관련
		"/api/auth/login",
		"/oauth2/**",
		"/login",
		"/logout",
		"/login/oauth2/code/**",

		// 유저 회원가입/조회 등 공개 API
		"/api/users",

		// 홈, 크롤링, 검색, 정적 페이지 등
		"/crawl",
		"/home",
		"/community",
		"/img/**",
		"/js/**",
		"/css/**",
		"/wallet.html",
		"/",

		// 결제 성공/실패
		"/api/payment/success",
		"/success-payment",
		"/fail-payment",
		"/payment",
		"/payment.html",

		// 헬스 체크, 모니터링
		"/actuator/**",

		// 검색 기능
		"/api/shop/search/**",

		// 웹소켓
		"/ws/**",
		"/connection/**",
		"/info",
		"/sockjs/**"
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

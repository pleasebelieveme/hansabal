package org.example.hansabal.common.config;

import org.example.hansabal.common.jwt.JwtFilter;
import org.example.hansabal.common.jwt.SecurityUrlMatcher;
import org.example.hansabal.common.oauth2.CustomOAuth2UserService;
import org.example.hansabal.common.oauth2.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/login", "/logout", "/oauth2/**", // 로그인 관련
								"/wallet.html",                   // 정적 페이지
								"/js/**", "/css/**", "/img/**",   // 리소스
								"/api/auth/login", "/api/auth/refresh" // 인증 API
						).permitAll()
				.requestMatchers(SecurityUrlMatcher.PUBLIC_URLS).permitAll()
				.requestMatchers(SecurityUrlMatcher.REFRESH_URL).authenticated()
				.requestMatchers(SecurityUrlMatcher.ADMIN_URLS).hasRole("ADMIN")
					.requestMatchers("/ws/**", "/connection/**", "/info", "/sockjs/**").permitAll()
					.requestMatchers("/api/users/me").authenticated()
				.requestMatchers("/write").authenticated()
				.anyRequest().authenticated()

			)
				.oauth2Login(oauth -> oauth
						.loginPage("/login")
						.userInfoEndpoint(user -> user.userService(customOAuth2UserService))
						.successHandler(oAuth2LoginSuccessHandler)
				)
				.logout(logout -> logout
						.logoutUrl("/logout")                         // 로그아웃 URL
						.logoutSuccessUrl("/home")                    // 로그아웃 성공 후 이동할 경로
						.invalidateHttpSession(true)                  // 세션 무효화
						.deleteCookies("accessToken")                 // accessToken 쿠키 삭제
				)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
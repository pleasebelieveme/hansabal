package org.example.anonymous.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

		http
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/api/auth/signup", "/api/users").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN")
				.requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")
				.anyRequest().authenticated()
			);

		http
			.csrf((auth) -> auth.disable());


		return http.build();
	}
}

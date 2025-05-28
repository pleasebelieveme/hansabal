package org.example.hansabal.common.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json;charset=UTF-8");

		String body = new ObjectMapper().writeValueAsString(Map.of(
			"status", 403,
			"error", "Unforbidden",
			"message", "접근 권한이 없습니다.",
			"path", request.getRequestURI()
		));

		response.getWriter().write(body);
	}
}

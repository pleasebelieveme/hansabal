package org.example.hansabal.common.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class)
			//@CurrentUser String userId나 @CurrentUser CustomAuthenticationToken auth만 지원
			&& (parameter.getParameterType().equals(CustomAuthenticationToken.class)
			|| parameter.getParameterType().equals(Long.class));
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		CustomAuthenticationToken auth = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

		if (parameter.getParameterType().equals(Long.class)) {
			return Long.parseLong(auth.getPrincipal().toString());
		}
		return auth;
	}
}
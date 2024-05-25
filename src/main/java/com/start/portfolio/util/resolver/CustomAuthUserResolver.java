package com.start.portfolio.util.resolver;

import com.start.portfolio.exception.UnauthorizedException;
import com.start.portfolio.util.CustomUserDetails;
import com.start.portfolio.util.annotation.CustomAuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class CustomAuthUserResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CustomAuthUser.class) &&
			parameter.getParameterType().equals(Long.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
			log.info("CustomUserResolver - 사용자 ID: {}", ((CustomUserDetails) authentication.getPrincipal()).getId());
			return ((CustomUserDetails) authentication.getPrincipal()).getId();
		}
		throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
	}
}

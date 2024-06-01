package com.start.portfolio.config.filter;

import com.start.portfolio.util.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws IOException {

		try {
			String token = jwtTokenProvider.resolveToken(request);

			if (token != null && jwtTokenProvider.validateToken(token)) {
				Authentication authentication = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			filterChain.doFilter(request, response);
		} catch (JwtException | IllegalArgumentException e) {
			log.error("JWT 처리 중 예외 발생: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않거나 만료된 토큰입니다.");
		} catch (Exception e) {
			log.error("필터 처리 중 예외 발생: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "예상치 못한 에러가 발생했습니다.");
		}
	}
}

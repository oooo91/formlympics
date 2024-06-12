package com.start.portfolio.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private static final String AUTHORIZATION = "AUTHORIZATION";
	public static final String BEARER_TYPE = "Bearer";
	private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/form/alarm/subscribe");

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.token-validation-in-seconds}")
	private long tokenValidTime;

	private final UserDetailsService userDetailsService;

	// 객체 초기화, secretKey를 Base64로 인코딩한다.
	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	// JWT 토큰 생성
	public String createToken(String username) { //username = email
		Claims claims = Jwts.claims().setSubject(username);
		Date now = new Date();

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(System.currentTimeMillis() + tokenValidTime))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	// JWT 토큰에서 인증 정보 조회
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	// 토큰에서 회원 정보 추출
	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest request) {

		// TODO SSE -> 클라이언트 측의 이벤트 소스는 헤더에 토큰을 세팅하는 것을 지원하지 않으므로, 파라미터로 토큰을 보냄 -> 특정 URI 에 대한 토큰 처리 필요
		if (TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
			log.info("Request with {} check the query param", request.getRequestURI());
			log.info("access token {} of query param", request.getQueryString().split("=")[1].trim());
			return request.getQueryString().split("=")[1].trim(); //token
		} else {
			String token = request.getHeader(AUTHORIZATION);
			if (!ObjectUtils.isEmpty(token) && token.toLowerCase()
				.startsWith(BEARER_TYPE.toLowerCase())) {
				return token.substring(BEARER_TYPE.length()).trim(); //token
			}
		}
		return null;
	}

	// 토큰의 유효성 + 만료일자 확인
	public boolean validateToken(String jwtToken) {
		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
		return !claims.getBody().getExpiration().before(new Date());
	}
}

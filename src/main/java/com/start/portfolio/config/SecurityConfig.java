package com.start.portfolio.config;

import com.start.portfolio.config.filter.JwtAuthenticationFilter;
import com.start.portfolio.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
		throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		HttpSecurity httpSecurity = http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(
				authorize -> authorize
					.requestMatchers(
						HttpMethod.POST, "/user/signup", "/user/signIn"
					).permitAll()
					.requestMatchers(
						HttpMethod.GET, "/form", "/alarm", "/main"
					).permitAll()
					.anyRequest().authenticated()
			)
			.formLogin(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 최근 Spring security -> 체이닝 방식 -> 람다 방식 선호
			)
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtTokenProvider), // Spring security 의 필터 체인 설정에서 필터 한 번만 추가됨
				UsernamePasswordAuthenticationFilter.class
			);

		return httpSecurity.build();
	}
}

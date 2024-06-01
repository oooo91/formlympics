package com.start.portfolio.util;

import com.start.portfolio.entity.User;
import com.start.portfolio.repository.UserCacheRepository;
import com.start.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final UserCacheRepository userCacheRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// TODO 캐싱한 데이터 없을 경우 -> DB 조회
		User user = userCacheRepository.getUser(username).orElseGet(
			() -> userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("사용자 정보가 없습니다."))
		);

		return CustomUserDetails.builder()
			.id(user.getId())
			.username(user.getEmail())
			.password(null)
			.authorities(null)
			.build();
	}
}

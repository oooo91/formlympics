package com.start.portfolio.util;

import com.start.portfolio.entity.order.User;
import com.start.portfolio.repository.order.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findById(Long.valueOf(username))
			.orElseThrow(() -> new UsernameNotFoundException("사용자 정보가 없습니다."));

		return CustomUserDetails.builder()
			.id(user.getId())
			.username(user.getEmail())
			.password(null)
			.authorities(null)
			.build();
	}
}

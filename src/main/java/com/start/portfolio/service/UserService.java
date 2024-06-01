package com.start.portfolio.service;

import com.start.portfolio.dto.AddressDto;
import com.start.portfolio.dto.MyInfoDto;
import com.start.portfolio.dto.RefundDto;
import com.start.portfolio.dto.UserDto;
import com.start.portfolio.dto.UserDto.SignInRequest;
import com.start.portfolio.entity.Address;
import com.start.portfolio.entity.Refund;
import com.start.portfolio.entity.User;
import com.start.portfolio.repository.AddressRepository;
import com.start.portfolio.repository.RefundRepository;
import com.start.portfolio.repository.UserCacheRepository;
import com.start.portfolio.repository.UserRepository;
import com.start.portfolio.util.JwtTokenProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final AddressRepository addressRepository;
	private final RefundRepository refundRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserCacheRepository userCacheRepository;


	@Transactional
	public void signup(UserDto.SignUpRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new RuntimeException("이미 가입된 아이디입니다.");
		}

		User user = request.toEntity();
		user.setPassword(passwordEncoder.encode(request.password()));
		log.info(user.toString());

		userRepository.save(user);
	}

	@Transactional
	public String signIn(SignInRequest request) {

		// TODO 로그인 시 아직 캐싱되어있다면 캐시에서 조회, 없다면 DB 조회 후 캐싱
		User user = userCacheRepository.getUser(request.email()).orElseGet(() -> {
			User fetchedUser = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new UsernameNotFoundException("사용자 정보가 없습니다."));
			userCacheRepository.setUser(fetchedUser);
			return fetchedUser;
		});

		// TODO 토큰 생성
		return jwtTokenProvider.createToken(user.getEmail());
	}

	@Transactional
	public void saveAddress(Long userId, AddressDto.Request request) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));
		Address address = request.toEntity();
		address.setUser(user);
		addressRepository.save(address);

		user.setAddress(address);
	}

	@Transactional
	public void saveRefund(Long userId, RefundDto.Request request) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));
		Refund refund = request.toEntity();
		refund.setUser(user);
		refundRepository.save(refund);

		user.setRefund(refund);
	}

	@Transactional(readOnly = true)
	public MyInfoDto.Response getMyInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

		Optional<Refund> optionalRefund = refundRepository.findById(user.getRefund().getId());
		RefundDto.Response refundResponse = optionalRefund.map(Refund::toDto)
			.orElse(RefundDto.Response.builder().build());

		Optional<Address> optionalAddress = addressRepository.findById(user.getAddress().getId());
		AddressDto.Response addressResponse = optionalAddress.map(Address::toDto)
			.orElse(AddressDto.Response.builder().build());

		return MyInfoDto.Response.builder()
			.user(user.toDto())
			.refund(refundResponse)
			.address(addressResponse)
			.build();
	}

}

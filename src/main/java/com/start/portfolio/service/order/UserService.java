package com.start.portfolio.service.order;

import com.start.portfolio.dto.order.AddressDto;
import com.start.portfolio.dto.order.MyInfoDto;
import com.start.portfolio.dto.order.MyInfoDto.Response;
import com.start.portfolio.dto.order.RefundDto;
import com.start.portfolio.dto.order.UserDto;
import com.start.portfolio.entity.order.Address;
import com.start.portfolio.entity.order.Refund;
import com.start.portfolio.entity.order.User;
import com.start.portfolio.repository.order.AddressRepository;
import com.start.portfolio.repository.order.RefundRepository;
import com.start.portfolio.repository.order.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


	@Transactional
	public void signup(UserDto.Request request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new RuntimeException("이미 가입된 아이디입니다.");
		}

		User user = request.toEntity();
		user.setPassword(passwordEncoder.encode(request.password()));
		log.info(user.toString());

		userRepository.save(user);
	}

	@Transactional
	public void saveAddress(Long userId, AddressDto.Request request) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));
		Address address = request.toEntity();
		address.setUser(user);

		addressRepository.save(address);

	}

	@Transactional
	public void saveRefund(Long userId, RefundDto.Request request) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));
		Refund refund = request.toEntity();
		refund.setUser(user);

		refundRepository.save(refund);
	}

	@Transactional(readOnly = true)
	public MyInfoDto.Response getMyInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

		Optional<Refund> optionalRefund = refundRepository.findById(userId);
		RefundDto.Response refundResponse = optionalRefund.map(Refund::toDto)
			.orElse(RefundDto.Response.builder().build());

		Optional<Address> optionalAddress = addressRepository.findById(userId);
		AddressDto.Response addressResponse = optionalAddress.map(Address::toDto)
			.orElse(AddressDto.Response.builder().build());

		return MyInfoDto.Response.builder()
			.user(user.toDto())
			.refund(refundResponse)
			.address(addressResponse)
			.build();
	}
}

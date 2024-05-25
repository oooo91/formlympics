package com.start.portfolio.controller;

import com.start.portfolio.dto.AddressDto;
import com.start.portfolio.dto.MyInfoDto;
import com.start.portfolio.dto.RefundDto;
import com.start.portfolio.dto.UserDto;
import com.start.portfolio.service.UserService;
import com.start.portfolio.util.annotation.CustomAuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// TODO 회원가입
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody UserDto.SignUpRequest request) {
		userService.signup(request);
		return ResponseEntity.ok("회원가입 성공");
	}

	// TODO 로그인
	@PostMapping("/signIn")
	public String signIn(@RequestBody UserDto.SignInRequest request) {
		log.info("user email = {}", request.email());
		return userService.signIn(request);
	}

	// TODO 주소 기입
	@PostMapping("/address")
	public ResponseEntity<String> saveAddress(@CustomAuthUser Long userId,
		@RequestBody AddressDto.Request request) {
		log.info("들어오나..." + userId);
		userService.saveAddress(userId, request);
		return ResponseEntity.ok("주소저장 성공");
	}

	// TODO 환불계좌 정보 기입
	@PostMapping("/refund")
	public ResponseEntity<String> saveRefund(@CustomAuthUser Long userId,
		@RequestBody RefundDto.Request request) {
		userService.saveRefund(userId, request);
		return ResponseEntity.ok("환불계좌저장 성공");
	}

	// TODO 내 정보 확인
	@GetMapping("/myInfo")
	public MyInfoDto.Response getMyInfo(@CustomAuthUser Long userId) {
		return userService.getMyInfo(userId);
	}

}

package com.start.portfolio.controller;

import com.start.portfolio.dto.FormDto;
import com.start.portfolio.dto.OrdersDto;
import com.start.portfolio.service.FormService;
import com.start.portfolio.util.annotation.CustomAuthUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController // ResponseBody + Controller
@RequiredArgsConstructor
@RequestMapping("/form")
public class FormController {

	private final FormService formService;

	// TODO 판매폼 작성
	@PostMapping("/form")
	public ResponseEntity<String> saveForm(@CustomAuthUser Long userId,
		@RequestBody FormDto.Request request) {
		formService.saveForm(userId, request);
		return ResponseEntity.ok("판매 폼을 작성했습니다.");
	}

	// TODO 판매폼 조회
	@GetMapping("/form/{id}")
	public FormDto.Response getForm(@PathVariable(name = "id") Long formId) {
		return formService.getForm(formId);
	}

	// TODO 판매폼 수정
	@PutMapping("/form/{id}")
	public FormDto.Response modifyForm(@PathVariable(name = "id") Long formId,
		@RequestBody FormDto.Request request) {
		return formService.modifyForm(formId, request);
	}

	// TODO 상품 구매 -> 선착순 주문, 재고 감소
	@PostMapping("/order")
	public void order(@CustomAuthUser Long userId, @RequestBody List<OrdersDto.Request> requests) {
		formService.order(userId, requests);
	}

	// TODO 좋아요

	// TODO DM

}

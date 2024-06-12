package com.start.portfolio.controller;

import com.start.portfolio.dto.AlarmDto;
import com.start.portfolio.dto.AlarmDto.Response;
import com.start.portfolio.dto.FormDto;
import com.start.portfolio.dto.OrdersDto;
import com.start.portfolio.service.AlarmService;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController // ResponseBody + Controller
@RequiredArgsConstructor
@RequestMapping("/form")
public class FormController {

	private final FormService formService;
	private final AlarmService alarmService;

	// TODO 판매폼 작성
	@PostMapping("")
	public ResponseEntity<String> saveForm(@CustomAuthUser Long userId,
		@RequestBody FormDto.Request request) {
		formService.saveForm(userId, request);
		return ResponseEntity.ok("판매 폼을 작성했습니다.");
	}

	// TODO 판매폼 조회
	@GetMapping("/{id}")
	public FormDto.Response getForm(@PathVariable(name = "id") Long formId) {
		return formService.getForm(formId);
	}

	// TODO 판매폼 수정
	@PutMapping("/{id}")
	public ResponseEntity<String> modifyForm(@PathVariable(name = "id") Long formId,
		@CustomAuthUser Long userId,
		@RequestBody FormDto.Request request) {
		formService.modifyForm(userId, formId, request);
		return ResponseEntity.ok("판매 폼을 수정했습니다.");
	}

	// TODO 상품 구매 -> 선착순 주문, 재고 감소
	@PostMapping("/order")
	public ResponseEntity<String> order(@CustomAuthUser Long userId, @RequestBody List<OrdersDto.Request> requests) {
		formService.order(userId, requests);
		return ResponseEntity.ok("상품 주문이 완료되었습니다. 이체를 진행해주세요.");
	}

	// TODO 좋아요 누른 사람
	@PostMapping("/alarm/{id}")
	public void alarm(@RequestBody AlarmDto.Request request, @CustomAuthUser Long userId, @PathVariable(name = "id") Long formId) {
		formService.like(userId, formId, request);
	}

	// TODO 좋아요 받은 사람
	@GetMapping("/alarm")
	public List<Response> alarmList(@CustomAuthUser Long userId) {
		return formService.alarmList(userId);
	}

	@GetMapping("/alarm/subscribe")
	public SseEmitter subscribe(@CustomAuthUser Long userId) {
		return alarmService.connectAlarm(userId);
	}

	// TODO DM

}

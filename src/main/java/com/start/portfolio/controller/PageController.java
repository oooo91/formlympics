package com.start.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	// TODO 알람 페이지 test
	@GetMapping("/alarm")
	public String getAlarmPage() {
		return "/alarm";
	}

	// TODO 알람 페이지 test
	@GetMapping("/main")
	public String getLoginPage() {
		return "/main";
	}
}

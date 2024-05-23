package com.start.portfolio.controller;

import com.start.portfolio.dto.ResumeDto;
import com.start.portfolio.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResumeController {

	private final ResumeService resumeService;

	@PostMapping("/resume")
	public void postingResume(@RequestBody ResumeDto.Request request) {
		resumeService.postingResume(request);
	}

}

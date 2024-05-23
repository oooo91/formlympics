package com.start.portfolio.controller;

import com.start.portfolio.dto.ApplicationDto;
import com.start.portfolio.dto.FinishedDto;
import com.start.portfolio.dto.RecruitmentDto;
import com.start.portfolio.service.RecruitmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecruitmentController {

	private final RecruitmentService recruitmentService;

	@PostMapping("/recruitments")
	public void postingRecruitment(@RequestBody RecruitmentDto.Request request) {
		recruitmentService.postingRecruitment(request);
	}

	@GetMapping("/recruitments")
	public List<RecruitmentDto.Response> getRecruitmentList() {
		return recruitmentService.getRecruitmentList();
	}

	@GetMapping("/recruitments/{id}")
	public RecruitmentDto.Response getRecruitment(@PathVariable(name = "id") Long recruitmentId) {
		return recruitmentService.getRecruitment(recruitmentId);
	}

	@PutMapping("/recruitments/{id}")
	public RecruitmentDto.Response modifyRecruitment(@PathVariable(name = "id") Long recruitmentId,
		@RequestBody RecruitmentDto.Request request) {
		return recruitmentService.modifyRecruitment(recruitmentId, request);
	}

	@DeleteMapping("/recruitments/{id}")
	public void deleteRecruitment(@PathVariable(name = "id") Long recruitmentId,
		@RequestBody RecruitmentDto.Request request) {
		recruitmentService.deleteRecruitment(recruitmentId, request);
	}

	@PostMapping("/recruitments/{id}/applications")
	public void applyRecruitment(@PathVariable(name = "id") Long recruitmentId,
		@RequestBody ApplicationDto.Request request) {
		recruitmentService.applyRecruitment(recruitmentId, request);
	}

	@GetMapping("/recruitments/{id}/applications")
	public List<ApplicationDto.Response> getApplications(
		@PathVariable(name = "id") Long recruitmentId,
		@RequestParam(name = "companyMemberId") Long companyMemberId) {
		return recruitmentService.getApplications(recruitmentId, companyMemberId);
	}

	// 공고 마감
	@PostMapping("/recruitments/{id}/finished")
	public void finishedRecruitment(@PathVariable(name = "id") Long recruitmentId,
		@RequestBody FinishedDto request) {
		recruitmentService.finishedRecruitment(recruitmentId, request.getCompanyMemberId());
	}
}

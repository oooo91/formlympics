package com.start.portfolio.service;

import com.start.portfolio.dto.ResumeDto;
import com.start.portfolio.entity.Member;
import com.start.portfolio.entity.Resume;
import com.start.portfolio.repository.MemberRepository;
import com.start.portfolio.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeService {

	private final ResumeRepository resumeRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public void postingResume(ResumeDto.Request request) {
		// TODO 넘어온 회원정보가 VALID 한 회원 정보인지 검증
		Member member = memberRepository.findByLoginId(request.loginId())
			.orElseThrow(() -> new RuntimeException("회원정보 없음!!"));

		// TODO 이력서 저장
		Resume resume = request.toEntity();
		resume.open();
		resume.setMember(member);
		resumeRepository.save(resume);
	}
}

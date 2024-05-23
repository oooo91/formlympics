package com.start.portfolio.service;

import com.start.portfolio.dto.ApplicantInfo;
import com.start.portfolio.dto.ApplicationDto;
import com.start.portfolio.dto.RecruitmentDto;
import com.start.portfolio.dto.RecruitmentDto.Request;
import com.start.portfolio.dto.RecruitmentDto.Response;
import com.start.portfolio.entity.Application;
import com.start.portfolio.entity.CompanyMember;
import com.start.portfolio.entity.Education;
import com.start.portfolio.entity.Recruitment;
import com.start.portfolio.entity.Resume;
import com.start.portfolio.enums.ApplicationStatus;
import com.start.portfolio.enums.EducationLevel;
import com.start.portfolio.enums.RecruitmentStatus;
import com.start.portfolio.repository.ApplicationRepository;
import com.start.portfolio.repository.CompanyMemberRepository;
import com.start.portfolio.repository.RecruitmentRepository;
import com.start.portfolio.repository.ResumeRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true) // TODO 추가적인 락을 사용하지 않으므로 성능 최적화
public class RecruitmentService {

	private final RecruitmentRepository recruitmentRepository;
	private final CompanyMemberRepository companyMemberRepository;
	private final ResumeRepository resumeRepository;
	private final ApplicationRepository applicationRepository;

	@Transactional
	public void postingRecruitment(RecruitmentDto.Request request) {
		// TODO 이 회원이 가입회원인지 검증 필요
		CompanyMember companyMember = companyMemberRepository.findByLoginId(
			request.companyMemberId()).orElseThrow(() -> new RuntimeException("기업 회원 정보 없음"));

		// TODO 공고를 등록
		Recruitment recruitment = request.toEntity();
		recruitment.setCompanyMember(companyMember);
		recruitment.opening();
		log.info(recruitment.toString());

		recruitmentRepository.save(recruitment);
	}

	public List<RecruitmentDto.Response> getRecruitmentList() {
		List<Recruitment> recruitments = recruitmentRepository.findAllByStatus(
			RecruitmentStatus.OPEN);

		return recruitments.stream().map(Recruitment::toDto).toList();
	}

	public Response getRecruitment(Long recruitmentId) {
		return recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> new RuntimeException("해당하는 공고 없음")).toDto();
	}

	@Transactional
	public Response modifyRecruitment(Long recruitmentId, RecruitmentDto.Request request) {
		// TODO 이 공고의 기업인지 조회
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> new RuntimeException("해당하는 공고 없음"));

		// TODO LONG 타입과 같이 객체를 비교할 때는 == 가 아니라 Objects.equals() 를 사용, == 는 참조 비교, equals 는 내용 비교 및 NPE 방지
		if (!Objects.equals(recruitment.getCompanyMember().getLoginId(),
			request.companyMemberId())) {
			throw new RuntimeException("잘못된 기업회원 정보 입니다.");
		}
		return recruitment.update(request).toDto();
	}

	@Transactional
	public void deleteRecruitment(Long recruitmentId, Request request) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> new RuntimeException("해당하는 공고 없음"));

		if (!Objects.equals(recruitment.getCompanyMember().getLoginId(),
			request.companyMemberId())) {
			throw new RuntimeException("잘못된 기업회원 정보 입니다.");
		}
		recruitmentRepository.deleteById(recruitmentId);
	}

	@Transactional
	public void applyRecruitment(Long recruitmentId, ApplicationDto.Request request) {
		//TODO 개인회원이면서 VALID 한 이력서 정보이다.
		Resume resume = resumeRepository.findByIdAndMemberId(request.resumeId(), request.memberId())
			.orElseThrow(() -> new RuntimeException("이력서 정보를 찾을 수 없습니다."));

		//TODO 존재하는 공고여야 한다.
		Recruitment recruitment = recruitmentRepository.findByIdAndStatus(recruitmentId,
			RecruitmentStatus.OPEN).orElseThrow(() -> new RuntimeException("해당하는 공고 없음"));

		Application application = Application.builder()
			.recruitment(recruitment)
			.resume(resume)
			.status(ApplicationStatus.APPLY_FINISHED)
			.build();

		applicationRepository.save(application);
	}

	public List<ApplicationDto.Response> getApplications(Long recruitmentId, Long companyMemberId) {
		// TODO valid 한 기업회원 정보인지
		companyMemberRepository.findById(companyMemberId)
			.orElseThrow(() -> new RuntimeException("조회 권한 없음"));

		// TODO 지원자들 정보 조회
		List<Application> applicationList = applicationRepository.findAllByRecruitmentId(
			recruitmentId);
		return applicationList.stream().map(a -> ApplicationDto.Response.builder()
				.applicationId(a.getId())
				.status(a.getStatus())
				.appliedDate(a.getAppliedDate())
				.resumeId(a.getResume().getId())
				.resumeTitle(a.getResume().getTitle())
				.educationList(a.getResume().getEducationList())
				.name(a.getResume().getMember().getName())
				.build())
			.toList(); // TODO toList() -> 불변한 list 생성, 그게 아닐 경우엔 .collect(Collectors.toList()); 사용
	}

	@Transactional
	public void finishedRecruitment(Long recruitmentId, Long companyMemberId) {
		// TODO VALID 한 공고 조회
		Recruitment recruitment = recruitmentRepository.findByIdAndStatusAndCompanyMemberId(
				recruitmentId, RecruitmentStatus.OPEN, companyMemberId)
			.orElseThrow(() -> new RuntimeException("공고 정보 없음"));

		// TODO 공고 마감 상태로 변경
		recruitment.closing();

		// TODO 지원자 정보를 조회
		List<Application> applicationList = recruitment.getApplicationList();

		// TODO 지원자 정보 가공 (점수 계산, 정렬)
		List<ApplicantInfo> applicantInfoList = applicationList.stream().map(Application::getResume)
			.map(r -> {
				Education education = r.getEducationList().stream()
					.max(Comparator.comparing(Education::getDegree))
					.orElse(Education.builder().build());
				return new ApplicantInfo(r.getId(),
					EducationLevel.findByCode(education.getCode()).getScore(education.getDegree()));
			}).sorted(Comparator.comparing(ApplicantInfo::getScore).reversed()).toList();

		Map<Long, Integer> applicationInfoMap = IntStream.range(0, applicantInfoList.size()).boxed()
			.collect(Collectors.toMap(i -> applicantInfoList.get(i).getResumeId(), i -> i)); //key : resumeId, value : value

		// TODO 공고에 설정한 합격자 수만큼 합격 처리, 나머지는 불합격 처리
		applicationList.forEach(application -> {
			if (applicationInfoMap.get(application.getResume().getId()) < recruitment.getRecruiterCount()) {
				application.pass();
			} else {
				application.fail();
			}
		});
	}
}

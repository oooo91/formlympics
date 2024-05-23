package com.start.portfolio.entity;

import com.start.portfolio.dto.RecruitmentDto;
import com.start.portfolio.enums.RecruitmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Recruitment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recruitment_id")
	private Long id;
	private String title;
	private Integer recruiterCount;
	private LocalDateTime closingDate;

	@Enumerated(EnumType.STRING)
	private RecruitmentStatus status;
	@UpdateTimestamp
	private LocalDateTime modifyDate;
	@CreationTimestamp
	private LocalDateTime postingDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_member_id")
	private CompanyMember companyMember;

	// TODO 양방향 조회
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "recruitment") // 필드 값 ㄱ
	private List<Application> applicationList;

	@Builder
	public Recruitment(
		String title,
		Integer recruiterCount,
		LocalDateTime closingDate
	) {
		this.title = title;
		this.recruiterCount = recruiterCount;
		this.closingDate = closingDate;
	}

	public void opening() {
		this.status = RecruitmentStatus.OPEN;
	}
	public void closing() { this.status = RecruitmentStatus.CLOSE; }

	// TODO Dto 로 파싱
	public RecruitmentDto.Response toDto() {
		return RecruitmentDto.Response.builder()
			.recruitmentId(this.id)
			.title(this.title)
			.recruiterCount(this.recruiterCount)
			.closingDate(this.closingDate)
			.status(this.status)
			.modifyDate(this.modifyDate)
			.postingDate(this.postingDate)
			.companyMemberId(this.companyMember.getId())
			.companyName(this.companyMember.getCompanyName())
			.build();
	}

	// TODO Recruitment 업데이트 용
	public Recruitment update(RecruitmentDto.Request request) {
		this.title = request.title(); // record 의 문법, record에서는 자동으로 접근자 메서드가 생성, 접근자 메서드 호출
		this.recruiterCount = request.recruiterCount();
		this.closingDate = request.closingDate();
		this.status = request.status();

		return this;
	}
}

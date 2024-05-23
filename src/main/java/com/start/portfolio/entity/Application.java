package com.start.portfolio.entity;

import com.start.portfolio.enums.ApplicationStatus;
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
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "application_id")
	private Long id;
	@CreationTimestamp
	private LocalDateTime appliedDate;

	@Enumerated(EnumType.STRING) //TODO EnumType.STRING 적용 안 하면 열거 순서 (기본값) 저장됨
	private ApplicationStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recruitment_id")
	private Recruitment recruitment;

	//이력서 하나로 지원 가능함 -> OneToOne
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resume_id")
	private Resume resume;

	@Builder
	Application(
		Resume resume,
		Recruitment recruitment,
		ApplicationStatus status
	) {
		this.resume = resume;
		this.recruitment = recruitment;
		this.status = status;
	}

	public void pass() {
		this.status = ApplicationStatus.PASS;
	}

	public void fail() {
		this.status = ApplicationStatus.FAIL;
	}
}

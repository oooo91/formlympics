package com.start.portfolio.entity;

import com.start.portfolio.enums.ResumeStatus;
import com.start.portfolio.util.EducationListJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resume_id")
	private Long id;
	private String title;

	// TODO Education 객체로 매핑
	@Convert(converter = EducationListJsonConverter.class)
	@Column(columnDefinition = "TEXT") // H2 는 JSON 타입을 지원하지 않으므로, TEXT 로 매핑하도록 컨버터 적용
	private List<Education> educationList;

	@Enumerated(EnumType.STRING)
	private ResumeStatus status;

	@UpdateTimestamp
	private LocalDateTime modifyDate;
	@CreationTimestamp
	private LocalDateTime postingDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	Resume(
		String title,
		List<Education> educationList
	) {
		this.title = title;
		this.educationList = educationList;
	}

	public void open() {
		this.status = ResumeStatus.OPEN;
	}
}

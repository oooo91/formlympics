package com.start.portfolio.dto;

import com.start.portfolio.entity.Education;
import com.start.portfolio.entity.Resume;
import java.util.List;

public class ResumeDto {

	public record Request(
		String title,
		List<EducationDto> educationList,
		String loginId
	) {

		public Resume toEntity() {
			return Resume.builder()
				.title(title)
				.educationList(educationList.stream().map(e -> Education.
						builder()
						.school(e.school)
						.degree(e.degree)
						.build())
						.toList())
					.build();
		}
	}

	// TODO 위 Request record 에 Education (엔티티) 을 그대로 사용하면, 단일 책임 원칙에 어긋나는 것이기 때문에 Education 을 위한 dto 를 만들자.
	public record EducationDto(
		String school,
		Integer degree
	) {}

}

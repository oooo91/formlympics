package com.start.portfolio.dto;

import com.start.portfolio.entity.Education;
import com.start.portfolio.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class ApplicationDto {

	public record Request(
		Long memberId,
		Long resumeId
	) {}

	@Builder
	@Getter // Getter 도 쓸 것 같앵
	public static class Response {
		private Long applicationId;
		private ApplicationStatus status;
		private LocalDateTime appliedDate;
		private Long resumeId;
		private String resumeTitle;
		private List<Education> educationList;
		private String name; //Member entity 의 name
	}
}

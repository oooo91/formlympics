package com.start.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.start.portfolio.entity.Recruitment;
import com.start.portfolio.enums.RecruitmentStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

public class RecruitmentDto {
	/**
	@ToString
	@Setter //데이터를 받기 위함
	public static class Request { //RecruitmentDto 인스턴스 없이 내부 클래스 접근 못하므로 static 붙임
		private String title;
		private String recruiterCount;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		private LocalDateTime closingDate;
		private String companyMemberLoginId;
	}
	 */

	// 자바 14 버전에서 레코드가 등장 -> 인스턴스 필요, 대신 getter 자동 생성, final 자동 생성
	public record Request(
		String title,
		Integer recruiterCount,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime closingDate,
		String companyMemberId,
		RecruitmentStatus status
	) {
		public Recruitment toEntity() {
			return Recruitment.builder()
				.title(title)
				.recruiterCount(recruiterCount)
				.closingDate(closingDate)
				.build();

		}
	}

	// TODO Response 객체는 record 가 아닌 inner class 로 작성, record 는 불변(immutable) 이기 때문에 빌더 패턴을 사용할 수 없다.
	@Builder
	@Getter
	public static class Response {
		private Long recruitmentId;
		private String title;
		private Integer recruiterCount;
		private LocalDateTime closingDate;
		private RecruitmentStatus status;
		private LocalDateTime modifyDate;
		private LocalDateTime postingDate;
		private String companyName;
		private Long companyMemberId;
	}

}

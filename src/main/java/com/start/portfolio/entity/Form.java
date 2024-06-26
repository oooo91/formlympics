package com.start.portfolio.entity;

import com.start.portfolio.dto.FormDto;
import com.start.portfolio.dto.FormDto.Request;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.springframework.util.ObjectUtils;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Form {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "form_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private User user;

	@OneToMany(mappedBy = "form", fetch = FetchType.LAZY)
	private List<Image> image;

	@OneToMany(mappedBy = "form", fetch = FetchType.LAZY)
	private List<Product> product;

	@OneToMany(mappedBy = "form", fetch = FetchType.LAZY)
	private List<Cart> cart;

	private String title;
	private String content;
	private Long likes;
	private LocalDateTime depositStartDate;
	private LocalDateTime depositEndDate;

	@Builder
	Form(
		String title,
		String content,
		Long likes,
		LocalDateTime depositStartDate,
		LocalDateTime depositEndDate
	) {
		this.title = title;
		this.content = content;
		this.likes = likes;
		this.depositStartDate = depositStartDate;
		this.depositEndDate = depositEndDate;
	}

	// TODO Dto 로 파싱
	public FormDto.Response toDto() {
		return FormDto.Response.builder()
			.title(this.title)
			.content(this.content)
			.likes(this.likes)
			.depositStartDate(this.depositStartDate)
			.depositEndDate(this.depositEndDate)
			.build();
	}

	public void increaseLike() {
		if (ObjectUtils.isEmpty(this.likes)) {
			this.likes = 0L; // 초기값 설정
		}
		this.likes = this.likes + 1; // 현재 값에 1을 더하여 다시 Long 객체로 할당
	}

	public void decreaseLike() {
		if (!ObjectUtils.isEmpty(this.likes)) {
			this.likes = this.likes - 1;
		}
	}

	// TODO Form 업데이트
	public void update(Request request) {
		this.title = request.title();
		this.content = request.content();
		this.depositStartDate = request.depositStartDate();
		this.depositEndDate = request.depositEndDate();
	}
}

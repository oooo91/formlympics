package com.start.portfolio.entity;

import com.start.portfolio.dto.FormDto;
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
	private List<Cart> carts;

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

	// TODO Form 업데이트
	public Form update(FormDto.Request request) {
		this.title = request.title();
		this.content = request.content();
		this.depositStartDate = request.depositStartDate();
		this.depositEndDate = request.depositEndDate();

		return this;
	}
}

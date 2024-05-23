package com.start.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //protected 접근 제어자를 가진 기본 생성자
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;
	private String name;
	private String loginId;


	// TODO @Builder -> setter 및 해당 객체를 반환하는 build() 메서드를 생성하므로 직접적으로 return 문을 작성하지 않아도 된다.
	@Builder
	Member(String name, String loginId) {
		this.name = name;
		this.loginId = loginId;
	}
}

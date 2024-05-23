package com.start.portfolio.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Education {
	private Integer code; // 학교 코드, 서울대학교 : 0, 연세대학교 : 1
	private String school;
	private Integer degree; // 고졸: 0, 대졸: 1, 석사: 2, 박사: 3
}

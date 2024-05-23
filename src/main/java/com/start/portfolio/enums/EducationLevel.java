package com.start.portfolio.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EducationLevel {

	SEOUL_UNIV(0, "서울대학교", i -> 10 * i),
	YONSEI_UNIV(1, "연세대학교", i -> 9 * i),
	KOREA_UNIV(2, "고려대학교", i -> 8 * i),
	KAIST_UNIV(3, "카이스트", i -> 7 * i),
	HANYANG_UNIV(4, "한양대학교", i -> 6 * i),
	SOGANG_UNIV(5, "서강대학교", i -> 5 * i),
	SUNGKYNHWAN_UNIV(6, "성균관대학교", i -> 4 * i),
	KONKOK_UNIV(7, "건국대학교", i -> 3 * i),
	SEJONG_UNIV(8, "세종대학교", i -> 2 * i),
	KYUNGPOOK_UNIV(9, "경북대학교", i -> 1 * i),
	SEOUL_HIGH(10, "서울고등학교", i -> 0 * i);


	private int code;
	private String name;
	private Function<Integer, Integer> score;

	public Integer getScore(Integer degree) {
		return score.apply(degree);
	}

	public static EducationLevel findByCode(int code) {
		return Arrays.stream(EducationLevel.values()).filter(e -> Objects.equals(e.code, code))
			.findFirst().orElseThrow(() -> new RuntimeException("잘못된 코드값"));
	}
}

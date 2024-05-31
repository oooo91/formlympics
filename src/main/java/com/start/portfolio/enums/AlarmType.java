package com.start.portfolio.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
	NEW_LIKE_FORM("new_like!");

	private final String alarmText;

}

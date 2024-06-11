package com.start.portfolio.kafka.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AlarmTopic {
	ALARM_REQUEST("alarm");

	private final String name;
}

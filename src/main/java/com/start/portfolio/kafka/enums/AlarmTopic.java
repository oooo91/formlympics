package com.start.portfolio.kafka.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AlarmTopic {
	ALARM_REQUEST("ALARM");

	private final String name;
}

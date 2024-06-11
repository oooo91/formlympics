package com.start.portfolio.kafka.dto;

import com.start.portfolio.entity.args.AlarmArgs;
import com.start.portfolio.enums.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmEvent {

	private Long receiverUserId;
	private AlarmType alarmType;
	private AlarmArgs alarmArgs;
}

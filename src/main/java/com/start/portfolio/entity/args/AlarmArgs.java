package com.start.portfolio.entity.args;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmArgs {

	// TODO 알람 발생시킨 사람
	private Long fromUserId;
	private Long formId;
}

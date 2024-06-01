package com.start.portfolio.entity.args;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmArgs {

	// TODO 알람 발생시킨 사람
	private Long fromUserId;
	private Long formId;

	@Builder
	AlarmArgs(
		Long fromUserId,
		Long formId
	) {
		this.fromUserId = fromUserId;
		this.formId = formId;
	}

}

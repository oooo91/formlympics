package com.start.portfolio.entity.args;

import com.start.portfolio.entity.User;
import com.start.portfolio.enums.AlarmType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
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

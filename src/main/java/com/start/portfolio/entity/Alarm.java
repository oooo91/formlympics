package com.start.portfolio.entity;

import com.start.portfolio.dto.AlarmDto;
import com.start.portfolio.entity.args.AlarmArgs;
import com.start.portfolio.enums.AlarmType;
import com.start.portfolio.util.converter.AlarmArgsConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alarm", indexes = {
	@Index(name = "idx_user_id", columnList = "user_id")
})
public class Alarm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_id")
	private Long id;

	// TODO 알람 받은 유저
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	// TODO Class -> Json
	@Column(columnDefinition = "json")
	@Convert(converter = AlarmArgsConverter.class)
	private AlarmArgs alarmArgs;

	@Column(name = "registered_at")
	private LocalDateTime registeredAt;

	// TODO 알람 확장성 고려
	@Enumerated(EnumType.STRING)
	private AlarmType alarmType;

	@Builder
	Alarm(
		User user,
		AlarmArgs alarmArgs,
		LocalDateTime registeredAt,
		AlarmType alarmType
	) {
		this.user = user;
		this.alarmArgs = alarmArgs;
		this.registeredAt = registeredAt;
		this.alarmType = alarmType;
	}

	// TODO Dto 로 파싱
	public AlarmDto.Response toDto() {
		return AlarmDto.Response.builder()
			.id(this.id)
			.user(this.user)
			.alarmType(this.alarmType)
			.alarmArgs(this.alarmArgs)
			.registeredAt(this.registeredAt)
			.build();
	}
}

package com.start.portfolio.service;

import com.start.portfolio.entity.Alarm;
import com.start.portfolio.entity.User;
import com.start.portfolio.entity.args.AlarmArgs;
import com.start.portfolio.enums.AlarmType;
import com.start.portfolio.exception.SnsApplicationException;
import com.start.portfolio.repository.AlarmRepository;
import com.start.portfolio.repository.EmitterRepository;
import com.start.portfolio.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

	private final static Long DEFAULT_TIMEOUT = 60 * 1000 * 60L;
	private final static String ALARM_NAME = "alarm";
	private final EmitterRepository emitterRepository;
	private final UserRepository userRepository;
	private final AlarmRepository alarmRepository;

	//TODO receiverUserId 가 페이지에 접속한 상태여야 연결 인스턴스를 가지므로 EmitterRepository 의 get() 을 Optional 로 감싼다.
	public void send(AlarmType type, AlarmArgs alarmArgs, Long receiverUserId) {
		//alarm => save
		User user = userRepository.findById(receiverUserId)
			.orElseThrow(() -> new SnsApplicationException("사용자가 존재하지 않습니다."));
		Alarm alarm = alarmRepository.save(Alarm.of(user, type, alarmArgs));

		//sse => push
		emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
			try {
				sseEmitter.send(SseEmitter.event().id(alarm.getId().toString()).name(ALARM_NAME)
					.data(alarmArgs.getFormUserName() + "이 " + alarmArgs.getFormTitle()
						+ "에 좋아요를 눌렀습니다."));
			} catch (IOException e) {
				emitterRepository.delete(receiverUserId);
				throw new RuntimeException("알람에 문제가 발생했습니다.");
			}
		}, () -> log.info("{} 가 접속하지 않은 상태입니다.", receiverUserId));
	}

	// TODO SSE 생성 및 반환 (아래는 connect 되었을 때 connect 되었다고 이벤트 전송)
	public SseEmitter connectAlarm(Long userId) {
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
		emitterRepository.save(userId, sseEmitter);

		sseEmitter.onCompletion(() -> emitterRepository.delete(userId)); //끝났을 경우
		sseEmitter.onTimeout(() -> emitterRepository.delete(userId)); //타임아웃일 경우

		try {
			sseEmitter.send(
				SseEmitter //SSE 는 브라우저 connect 당 하나의 SSE 인스턴스 생성 (연결 생성)
					.event()
					.id("id")
					.name(ALARM_NAME)
					.data("connect completed"));
		} catch (IOException e) {
			throw new RuntimeException("알람에 문제가 발생했습니다.");
		}
		return sseEmitter;
	}
}

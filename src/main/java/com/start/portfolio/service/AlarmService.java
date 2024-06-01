package com.start.portfolio.service;

import com.start.portfolio.repository.EmitterRepository;
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

	//TODO seller_id 가 브라우저에 접속한 상태여야 인스턴스를 만들어서 가지게 되므로 EmitterRepository 의 get() 을 Optional 로 감싼다.
	public void send(Long alarmId, Long userId) { //알람, 보낼 애
		emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
			try {
				sseEmitter.send(SseEmitter.event().id(alarmId.toString()).name(ALARM_NAME).data("new alarm"));
			} catch (IOException e) {
				emitterRepository.delete(userId); //seller_id 에 보낼 때 문제가 생겼다면 굳이 seller_id SSE 저장할 필요 없음
				throw new RuntimeException("알람에 문제가 발생했습니다.");
			}
		}, () -> log.info("seller_id 가 접속하지 않은 상태입니다."));
	}

	// TODO SSE 생성 및 반환 (아래는 connect 되었을 때 connect 되었다고 이벤트 전송)
	public SseEmitter connectAlarm(Long userId) { //내가 만든 connect
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
		emitterRepository.save(userId, sseEmitter);

		sseEmitter.onCompletion(() -> emitterRepository.delete(userId)); //끝났을 경우
		sseEmitter.onTimeout(() -> emitterRepository.delete(userId)); //타임아웃일 경우

		try {
			sseEmitter.send(
				SseEmitter //SSE 는 브라우저 connect 당 하나의 인스턴스가 생성된다. -> 즉 해당 알람이 일어난 브라우저 (SSE) 를 찾아야한다. -> 따라서 SSE 인스턴스를 저장할 클래스가 필요하다.
				.event()
				.id("id") // 이벤트의 ID 설정, 이벤트 ID -> 서버가 클라이언트에 전송하는 각 이벤트에 부여하는 고유한 식별자 -> 클라이언트는 이 ID 로 중단된 시점 이후의 이벤트를 다시 받을 수 있다. (브라우저의 EventSource 객체는 이벤트 ID 를 자동 처리함, 내부적으로 마지막으로 수신한 이벤트 ID 를 기억함)
				.name(ALARM_NAME) // 이벤트의 이름 설정, 클라이언트가 구독한 이벤트에 대한 전송임을 알린다.
				.data("connect completed")); // 이벤트의 데이터 설정, 단순히 "connect completed"라는 메시지를 전송했다.
		} catch (IOException e) {
			throw new RuntimeException("알람에 문제가 발생했습니다.");
		}
		return sseEmitter;
	}
}

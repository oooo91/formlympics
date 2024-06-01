package com.start.portfolio.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Repository
public class EmitterRepository {

	// 인스턴스 자체를 저장해야 해당 인스턴스에 connect 가 되므로, 데이터베이스 (RDS, REDIS) 에 저장하는 것은 불가능
	// TODO Local Cache 로 인스턴스 저장 -> 단, 분산 서버에서는 어려움 (user1 한테 알람이 가야하는데, 알람이 생성된 게 서버2라면 서버2에는 user1의 브라우저가 없으므로 보내줄 수 x)
	// 따라서 전체 서버 인스턴스에 user1에 알람이 발생했음을 알림 -> 인스턴스 중 user1(브라우저)에 커넥트된 서버 인스턴스가 이를 확인하도록 구현해야함 -> 아직 고려 x
	private final Map<String, SseEmitter> emitterMap = new HashMap<>();

	// TODO 알람을 받는 유저 ID (seller_id) 로 접속한 브라우저를 찾아야한다.
	public SseEmitter save(Long userId, SseEmitter sseEmitter) {
		final String key = getKey(userId);
		emitterMap.put(key, sseEmitter);

		log.info("Set sseEmitter {}", userId);
		return sseEmitter;
	}

	public Optional<SseEmitter> get(Long userId) {
		final String key = getKey(userId);
		log.info("Get sseEmitter {}", userId);

		return Optional.ofNullable(emitterMap.get(key));
	}

	public void delete(Long userId) {
		emitterMap.remove(getKey(userId));
	}

	private String getKey(Long userId) {
		return "Emitter:UID:" + userId;
	}
}

package com.start.portfolio.redis.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.portfolio.entity.Alarm;
import com.start.portfolio.entity.User;
import com.start.portfolio.entity.args.AlarmArgs;
import com.start.portfolio.enums.AlarmType;
import com.start.portfolio.exception.SnsApplicationException;
import com.start.portfolio.kafka.dto.AlarmEvent;
import com.start.portfolio.repository.AlarmRepository;
import com.start.portfolio.repository.EmitterRepository;
import com.start.portfolio.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

	private final static String ALARM_NAME = "alarm";
	private final EmitterRepository emitterRepository;
	private final UserRepository userRepository;
	private final AlarmRepository alarmRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		log.info("Redis subscribe started");
		try {
			AlarmEvent chatMessage = objectMapper.readValue(message.getBody(), AlarmEvent.class);

			Long receiverUserId = chatMessage.getReceiverUserId();
			AlarmType type = chatMessage.getAlarmType();
			AlarmArgs alarmArgs = chatMessage.getAlarmArgs();

			// 사용자 찾기
			User user = userRepository.findById(receiverUserId)
				.orElseThrow(() -> new SnsApplicationException("사용자가 존재하지 않습니다."));

			// 알람 저장
			Alarm alarm = alarmRepository.save(Alarm.of(user, type, alarmArgs));
			log.info("저장이 되었는가요오오오오오오 {}", alarm);

			// SSE 이벤트 보내기
			emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
				try {
					sseEmitter.send(SseEmitter.event().id(alarm.getId().toString()).name(ALARM_NAME)
						.data(alarmArgs.getFormUserName() + "이 " + alarmArgs.getFormTitle()
							+ "에 좋아요를 눌렀습니다."));
					log.info(alarmArgs.getFormUserName() + "이 " + alarmArgs.getFormTitle() + "에 좋아요를 눌렀습니다.");
				} catch (IOException e) {
					emitterRepository.delete(receiverUserId);
					throw new RuntimeException("알람에 문제가 발생했습니다.");
				}
			}, () -> log.info("{} 가 접속하지 않은 상태입니다.", receiverUserId));
			log.info("Redis subscribe finished");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
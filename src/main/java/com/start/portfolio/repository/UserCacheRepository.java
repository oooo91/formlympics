package com.start.portfolio.repository;

import com.start.portfolio.entity.User;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

	private final RedisTemplate<String, User> userRedisTemplate;
	private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

	public void setUser(User user) {
		String key = getKey(user.getEmail());
		log.info("set user to redis {} , {}", key, user);
		userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
	}

	public Optional<User> getUser(String username) { //username = email
		String key = getKey(username);
		User user = userRedisTemplate.opsForValue().get(key);
		log.info("get data from redis {} , {}", key, user);
		return Optional.ofNullable(user);
	}

	// TODO 유저 -> 필터에 가장 많이 사용 -> 이 부분 캐싱으로 대체
	private String getKey(String username) {
		// TODO 어떤 키인지 구분하기 위해 prefix 붙여넣기 (ex. 유저 정보를 저장하는 키)
		return "USER:" + username;
	}
}

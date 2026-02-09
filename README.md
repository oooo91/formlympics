### 프로젝트 명 : 폼림픽

2024.05.20 ~ 2024.06.13

빠른 시간 내에 폼으로 주문하여 선착순으로 상품을 구매하는 C2C 플랫폼 입니다.
<br>
동시성, 인증/인가, 캐시, 비동기 처리 등 서버 개발에서 자주 마주치는 문제들을 구현하며 개념을 익히는 것을 목표로 진행했습니다.

### ERD
<img src="https://github.com/oooo91/formlympics/assets/74234719/3daf341b-d440-4cdf-a2f6-9a76aaee1bf8" alt="Image" width="900"/>


### 학습 과정에서 적용한 주요 기술

- **Redis 캐시로 DB 성능 최적화**
    - 상황 → Token 인증 시 user 를 조회하고, 비즈니스 로직에서 다시 user 를 조회하는 구조로 DB I/O 증가
    - 해결 → 서비스 상 유저 정보는 거의 변하지 않으므로 Token 인증 시 캐시에서 user 를 조회하도록 변경
      <br>
      
        <img src="https://github.com/oooo91/formlympics/assets/74234719/3dad30bd-0788-4a64-a4ea-f58b95c4e6d0" alt="Image" width="700"/>
- **Resolver를 적용한 인증 권한 예외 처리**
    - 상황 → Controller 에서 매번 Authentication 으로부터 유저ID 를 직접 추출 → 코드 중복
    - 해결
        - 모든 요청에 대해 일괄적으로 처리하는 Interceptor 보다, 인증 객체가 필요한 특정 API 에 대해서만 인증 정보가 필요 → Resolver 적용
            - 파라미터 타입이 맞다면 Resolver 로부터 자동으로 유저ID 추출
            - Authentication 이 존재하지 않을 경우 Resolver 에서 예외처리
- **AOP 를 적용한 공통 관심사 분리**
    - 상황 → 대부분의 서비스는 로그인을 해야 이용할 수 있으므로 서비스마다 log 적용 → 코드 중복
    - 해결 → AOP 로 횡단관심사 분리하여 중복 코드 제거

- **상품 수량 감소 시 발생하는 동시성 문제 해결**
    - 상황 → 주문 요청이 동시에 여러 번 실행됐을 경우 테스트 실패
    - 해결
        - synchronized, RDB Lock 과 Redis 고려
            - synchronized → 단일 JVM 에서 동작
            - Optimistic lock → 업데이트 작업이 빈번하므로 고려하지 않음
            - Pessimistic lock → 락을 오래 걸수록 성능 저하되며 데이터베이스 클러스터 환경에서 락 관리가 어려움
            - Redis → 인메모리 저장소로 빠르며 클러스터 모드를 제공하므로 분산 환경 배포 시 용이함
            - 락을 필요 시 하는 구간이 짧으며 DB 클러스터 모드를 고려하지 않으므로 pessimistic lock 사용
- **데이터 무결성을 위한 트랜잭션 적용**
    - 상황 → 요청 건 별로 트랜잭션이 정상적으로 작동되었는지 로그로 확인 불가능
    - 해결
        - 주문 실패 시에도 실패한 로그를 확인하기 위해 별도의 트랜잭션 분리
            - 새로운 물리 트랜잭션을 사용하도록 @Transactional 의 propagation 속성을 `REQUIRES_NEW` 로 설정
- **선착순 쿠폰 지급을 위해 Redisson 적용하여 동시성 제어 및 성능 최적화**
    - 상황 → 선착순으로 쿠폰 발급 시 쿠폰 n 개보다 더 많은 쿠폰이 이용자에게 지급
    - 해결
        - 정확한 쿠폰 발급과 DB 정합성을 위해 RDS Lock 과 Redis 를 고려
            - 쿠폰 로직은 걸어야하는 락 구간이 길기 때문에 RDS Lock 적용 시 성능 불이익
            - 인 메모리인 Redis 의 Redisson 으로 락 구현
- **SSE 를 활용하여 알림 처리 구현**
    - 상황 → 주기적으로 API 를 호출해야 알람을 받을 수 있음
    - 해결
        - Polling, Long polling, SSE, web socket 고려
            - Polling → 주기적으로 API 를 호출하므로 부하가 발생할 수 있음
            - Long poliing → 요청을 보낸 후 업데이트 발생 시에 응답을 받을 수는 있으나, 업데이트 빈번하면 Polling 과 같이 부하가 발생하므로 적합하지 않다고 판단
            - SSE (Server-Sent Event) → 서버에서 웹 브라우저로 이벤트를 전송하는 단방향 통신으로, 지속적인 연결을 유지하면서도 Polling 방식보다 트래픽 부하가 적음
            - Web Socket → 실시간 양방향 통신
            - 양방향 통신을 고려하지 않아도 되는 알람 기능이므로 SSE 사용
- **분산 환경을 고려한 Redis Pub/Sub 고려**
    - 상황 → SSE는 단일 서버와의 연결을 통해 실시간 데이터를 전송하는 데 사용하므로 분산 환경에서 상태 관리가 어려움
    - 해결 → Redis Pub/Sub 을 사용하여 SSE 를 저장하고 있는 해당 WAS 가 응답하도록 함
- **알람 비동기 처리**
    - 상황 → 알람이 완성되어야 좋아요 기능이 정상적으로 처리
        - 알람은 동기적으로 전송될 필요 없음 → 비즈니스 로직 분리 필요
    - 해결
        - Kafka, RabbitMQ, Redis Pub/Sub 고려
            - RabbitMQ → 수평확장이 어려움
            - Redis Pub/Sub →  메시지의 손실이나 중복 전달 가능
            - 선착순 쿠폰 지급 로직에서도 알람 사용 → 대량의 트래픽이 발생할 것을 고려하여 분산 처리 및 중복성 없는 전송이 가능한 Kafka 를 채택하여 알람 처리
      <img src="https://github.com/oooo91/formlympics/assets/74234719/052d01c4-4696-4307-8b9e-aa1f83a7acd5" alt="Image" width="700"/>

### 트러블 슈팅
- pessimistic lock 으로 구현한 부분을 redisson 으로 변경하는 과정에서 오류가 발생
- 해결
    - 락을 획득/해제는 트랜잭션의 단위보다 크게 이루어져야 하나, 반대로 트랜잭션이 먼저 시작돼서 문제가 발생한 것으로 예상된다.
    - 다음과 같이 락을 획득한 후 트랜잭션을 실행한도록 코드를 수정했다.
```
//RedissonLockCouponFacade 
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockCouponFacade {

	private final RedissonClient redissonClient;
	private final CouponService couponService;

	public void getCoupon(Long userId, Long couponId) {
		RLock lock = redissonClient.getLock(couponId.toString());
		try {
			boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS); //락을 시도하는 최대 시간, 락의 만료 시간(락을 획득하면 1초 동안 유지
			if (!available) {
				log.warn("LOCK 획득 실패");
				return;
			}

			log.info("LOCK 획득");
			couponService.getCoupon(userId, couponId);

		} catch (InterruptedException e) {
			log.warn("LOCK 획득 실패 - 예외 발생: {}", e.getMessage());
			throw new RuntimeException(e);
		} catch (Exception e) {
			log.error("예상치 못한 에러 발생: {}", e.getMessage());
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
			log.info("LOCK 해제");
		}
	}
}
```
```
//CouponService 
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

	private final UserRepository userRepository;
	private final UserCouponRepository userCouponRepository;
	private final CouponRepository couponRepository;

	@Transactional
	public void getCoupon(Long userId, Long couponId) {

		userCouponRepository.findByUserIdAndCouponId(
			userId, couponId).ifPresent(userCoupon -> {
			throw new CouponOutOfStockException("이미 쿠폰이 발급되었습니다.");
		});

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 쿠폰입니다."));

		Long issuedCouponCount = coupon.getIssuedCouponCount();
		//Long count = couponCountRepository.increment();
		log.info("race condition = {}", issuedCouponCount);

		if (issuedCouponCount >= coupon.getTotalCouponQuantity()) {
			return;
		}
		coupon.update();

		couponRepository.save(coupon);
		userCouponRepository.save(UserCoupon.builder()
			.user(user)
			.coupon(coupon)
			.build());
	}
}
```
 


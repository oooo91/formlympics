### 프로젝트 명 : 폼림픽

2024.05.20 ~

빠른 시간 내에 폼으로 주문하여 선착순으로 상품을 구매하는 C2C 플랫폼 입니다.

### ERD
<img src="https://github.com/oooo91/formlympics/assets/74234719/3daf341b-d440-4cdf-a2f6-9a76aaee1bf8" alt="Image" width="900"/>


### 담당 역할

- **가독성 향상**
    - record 를 도입하여 별도의 애노테이션 없이 코드 작성
    - 필요한 필드만 이용할 수 있도록 빌더 패턴 적용
- **읽기 전용 트랜잭션 및 Redis 캐시로 DB 성능 최적화**
    - `@Transactional(readOnly = true)` 추가적인 락을 사용하지 않으므로 성능 최적화
    - 중복 코드 최적화
        - 문제 → Token 인증 시 user 를 조회하고, 그 이후 비즈니스 로직에서 다시 user 를 조회하는 구조로 데이터베이스 I/O 증가
        - 해결 → 유저 정보 (이름, 패스워드)는 거의 변하지 않으므로 Redis 를 사용하여 캐싱 적용
        - 2번의 쿼리 → 1번의 쿼리
      <br>
      
        <img src="https://github.com/oooo91/formlympics/assets/74234719/3dad30bd-0788-4a64-a4ea-f58b95c4e6d0" alt="Image" width="700"/>

- **Resolver를 적용한 인증 권한 예외 처리**
    - 문제 → Controller 에서 직접 Authentication 으로부터 유저ID 를 추출
    - 해결
        - Custom Annotation 및 Resolver 를 적용
            - 파라미터 타입이 맞다면 Resolver 로부터 자동으로 유저ID 를 받도록 함
            - Authentication 이 존재하지 않을 경우 Resolver 에서 자동으로 예외 처리 진행하도록 구현
- **AOP 를 적용한 공통 관심사 분리**
    - 문제 → 대부분의 서비스는 로그인을 해야 이용할 수 있으므로 서비스마다 log 사용
    - 해결 → AOP 로 횡단관심사 분리 → 로그인 여부를 확인하는 중복 코드 제거
- **상품 수량 감소 시 발생하는 동시성 문제 해결**
    - 문제 → 주문하기 API 요청이 동시에 여러 번 실행됐을 경우 테스트 실패
    - 해결
        - synchronized, RDB Lock 과 Redis 고려
            - synchronized, RDB Lock → 분산 환경에 적합하지 않다고 판단
                - synchronized → 단일 JVM 에서만 동작함
                - Optimistic lock → 업데이트 작업이 빈번하므로 고려하지 않음
                - Pessimistic lock → 락을 오래 걸수록 성능 저하, timeout 설정이 까다로움, 데이터베이스 클러스터 환경에서 락 관리 어려움
                - Redis → 인메모리 저장소로 성능 빠름, 클러스터 모드를 제공하므로 추후 분산 환경 배포 시 용이함
                - 락을 필요 시 하는 구간이 짧으며 DB 클러스터 모드를 고려하지 않아도 되므로 pessimistic lock 으로 최종 결정
- **데이터 무결성을 위한 트랜잭션 적용**
    - 문제 → 요청 건 별로 정상적으로 트랜잭션이 적용되었는지 로그로 확인 불가능
    - 해결
        - 주문 실패 시 실패한 기록을 확인하기 위해 로그를 저장하는 로직을 별도의 트랜잭션으로 분리
        - 해당 로직이 새로운 물리 트랜잭션을 사용하도록 @Transactional 의 propagation 속성을 `REQUIRES_NEW` 로 설정
- **선착순 쿠폰 지급을 위해 Redisson 적용하여 동시성 제어 및 성능 최적화**
    - 문제 → 선착순으로 쿠폰 발급 시 쿠폰 n 개보다 더 많은 쿠폰이 이용자에게 지급
    - 해결
        - 정확한 쿠폰 발급과 DB 의 정합성을 위해 RDS Lock 과 Redis 를 고려
            - 위 재고는 재고에만 lock 걸어주면 되므로 (재고 감소 로직) pessimistic lock 으로 락 구현 → 쿠폰 로직은 걸어야하는 락 구간이 길기 때문에 성능 불이익 발생
            - 인 메모리인 Redis 의 Redisson 으로 락 구현
- **SSE 를 활용하여 알림 처리 구현**
    - Polling, Long polling, SSE, web socket 고려
        - Polling → 주기적으로 API 를 호출하므로 부하가 발생할 수 있음
        - Long poliing → 요청을 보낸 후 업데이트 발생 시에 응답을 받을 수는 있으나, 업데이트 빈번하면 Polling 과 같이 부하가 발생하므로 적합하지 않다고 판단
        - SSE (Server-Sent Event) → 서버에서 웹 브라우저로 이벤트를 전송하는 단방향 통신으로, 지속적인 연결을 유지하면서도, Polling 방식보다 트래픽 부하가 적음
        - Web socker → 실시간 양방향 통신
        - 양방향 통신을 고려하지 않아도 되는 알람 기능이므로 SSE 사용으로 최종 선택
- **Redis Pub/Sub 활용**
    - 상황 → SSE 는 로컬에서만 저장이 가능함
    - 해결 → Redis Pub/Sub 고려 중 
- **Kafka 로 알림 비동기 처리**
    - 상황 → 알람이 완성되어야 좋아요 기능이 정상적으로 처리됨
        - 알람 기능과 좋아요 기능이 강하게 결합되어있음
            - 알람은 동기적으로 전송될 필요 없음 → 비즈니스 로직 분리 필요
    - 해결
        - 메시지 처리를 kafka 에게 위임하여 비동기적으로 처리
            - 학습 차원에서 kafka 채택

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

### 추후 개발 및 기술적인 도전 계획
- 채팅 API
- 주문 내역 엑셀 다운로드 API
- JPA 학습 후 → QueryDSL 변경 및 성능 개선
- 모니터링
- NGINX 배포
 


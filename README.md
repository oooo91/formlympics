프로젝트 명 : 폼림픽

프로젝트 기간 : 2024.05.22 ~ (진행중)

빠른 시간 내에 폼으로 주문하여 선착순으로 상품을 구매하는 C2C 플랫폼 입니다.

- 가독성 향상
    - record 를 도입하여 별도의 애노테이션 없이 코드 작성
    - 필요한 필드만 이용할 수 있도록 빌더 패턴 적용
- 성능 최적화
    - `@Transactional(readOnly = true)` 추가적인 락을 사용하지 않으므로 성능 최적화
- Resolver 적용한 인증 권한 예외 처리
    - 문제 → Controller 에서 직접 Authentication 으로부터 유저ID 를 추출
    - 해결
        - Custom Annotation 및 Resolver 를 적용
            - 파라미터 타입이 맞다면 Resolver 로부터 자동으로 유저ID 를 받도록 함
            - Authentication 이 존재하지 않을 경우 Resolver 에서 자동으로 예외 처리 진행하도록 구현
- AOP 를 적용한 공통 관심사 분리
    - 문제 → 대부분의 서비스는 로그인을 해야 이용할 수 있으므로 서비스마다 log 사용
    - 해결 → AOP 로 횡단관심사 분리 → 로그인 여부를 확인하는 중복 코드 제거
- 분산 환경을 고려한 주문하기 API
    - 상품 수량 감소 동시성 처리
        - 문제 → 주문하기 API 요청이 동시에 100번 실행됐을 경우 테스트 실패
        - 해결
            - synchronized → 단일 JVM 에서만 동작하므로 분산 환경에 적합하지 않음
            - MySQL Pessimistic Lock 과 Named Lock, Redis 의 distributed lock 고려
                - 업데이트 작업이 빈번하므로 Optimistic lock 은 고려하지 않음
                - Pessimistic lock 을 사용할 경우, timeout 설정이 까다롭기 때문에 Named lock 선택 → Named Lock 또한 세션 관리가 복잡하므로 Redis 를 사용하기로 최종 결정
                - Redis 의 Redisson 으로 동시성 문제 해결
    - 데이터 무결성을 위한 트랜잭션 적용
        - 문제 → 요청 건 별로 정상적으로 트랜잭션이 적용되었는지 로그로 확인 불가능
        - 해결
            - 주문 실패 시 실패한 기록을 확인하기 위해 로그를 저장하는 로직을 별도의 트랜잭션으로 분리
            - 해당 로직이 새로운 물리 트랜잭션을 사용하도록 @Transactional 의 propagation 속성을 `REQUIRES_NEW` 로 설정

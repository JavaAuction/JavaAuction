# 🏦 JavaAuction – 실시간 경매 기반 중고 거래 플랫폼

<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)


[프로젝트 소개](#-프로젝트-개요) •
[기술 스택](#-기술-스택--인프라) •
[핵심 기능](#-핵심-기능--기술-구현) •
[성과](#-성능-개선-결과) •
[팀원](#-팀-구성)

</div>

---

## 📌 프로젝트 개요

### 💡 프로젝트 한 줄 소개
> **실시간 입찰, 즉시 구매, 인기 상품 조회까지 지원하는 MSA 기반 실시간 중고 경매 플랫폼**

### 🎯 프로젝트 컨셉
- **MSA + 이벤트 드리븐 아키텍처** 기반의 실전형 경매 서비스
- **Kafka, Redis, 분산락, 모니터링, 시큐어 코딩**을 실제 도메인에 풀 적용
- "단순 CRUD"가 아닌 **실시간성 + 정합성 + 성능 튜닝**까지 고려한 서비스 설계

### 🚀 주제 선정 배경 & 기획 의도

<details>
<summary><b>주제 선정 이유</b></summary>

- 중고 거래 시장이 확대되면서 단순 정가 거래를 넘어 **경매 방식**에 대한 수요 증가
- 실시간 입찰 경쟁, 가격 변동, 인기 상품 노출 등 **역동적인 거래 경험** 제공

</details>


</details>

---

## 🛠 기술 스택 & 인프라

### 🛠 Backend & Core

- **Language**: Java 17  
- **Framework**: Spring Boot 3.x (4.0.0 구조), Spring Data JPA, QueryDSL 5.0.0  
- **MSA**: Spring Cloud Gateway, Eureka, OpenFeign  
- **Messaging**: Apache Kafka  
- **Realtime**: SSE (Server-Sent Events)  
- **Cache**: Spring Cache, Redis  
- **Storage**: AWS S3  

---

### 🧱 Infra & DevOps

- **Database**: PostgreSQL, Redis  
- **Infra**: AWS EC2, Docker  
- **CI/CD**: GitHub Actions  
- **Monitoring**: Prometheus, Grafana  
- **Testing**: JMeter


---

## 👥 팀 구성

<table>
  <tr>
    <th>이름</th>
    <th>역할</th>
    <th>담당 업무</th>
  </tr>
  <tr>
    <td><b>한규원</b></td>
    <td>팀장</td>
    <td>유저·리뷰 서비스, CI/CD, 전체 아키텍처 총괄</td>
  </tr>
  <tr>
    <td><b>김이안</b></td>
    <td>팀원</td>
    <td>결제 서비스, 비관락/이벤트 기반 결제 처리</td>
  </tr>
  <tr>
    <td><b>신나리</b></td>
    <td>팀원</td>
    <td>알림·채팅 서비스, SSE 도입, Slack 연동</td>
  </tr>
  <tr>
    <td><b>이승언</b></td>
    <td>팀원</td>
    <td>상품 서비스, 조회수·인기 상품 구현, Redis 캐시</td>
  </tr>
  <tr>
    <td><b>이태경</b></td>
    <td>팀원</td>
    <td>경매 서비스, 입찰 로직, Kafka 이벤트 설계</td>
  </tr>
  <tr>
    <td><b>한성연</b></td>
    <td>팀원</td>
    <td>경매 서비스, 동시성 제어 및 분산락 전략</td>
  </tr>
</table>

---

## 🏗 시퀀스 다이어그램

**Flow Chart**

   


<img width="4096" height="1412" alt="image" src="https://github.com/user-attachments/assets/5e668760-5f09-4f24-a142-938e1b6036f0" />

---

## 🔍 도메인 다이어그램

<img width="1552" height="1211" alt="image" src="https://github.com/user-attachments/assets/5475373a-73e2-4af2-add8-5c2d8651a845" />


---

## 🎯 핵심 기능 & 기술 구현

### 1️⃣ 조회수 및 인기 상품

<details>
<summary><b>Redis ZSET 기반 조회수 / 인기 상품 캐싱</b></summary>

#### 목표
- 상품 조회수를 실시간에 가깝게 집계  
- 조회수 기준 **인기 상품 Top 10**을 빠르게 응답  

#### 데이터 구조 (Redis Sorted Set)
Key : product:view_ranking
Member: productId (문자열)
Score : viewCount (double)

- `ZINCRBY` 로 O(log N) 복잡도로 조회수 증가  
- `ZREVRANGE` 로 상위 N개의 상품 ID 조회  

#### 조회수 증가 플로우

```bash
클라이언트가 상품 상세 조회 API 호출

ProductController.getProduct()

ProductViewService.incrementViewCount(productId) ← ZINCRBY

ProductService.getProduct(productId) ← @Cacheable

Redis에서 조회수를 다시 가져와 DTO에 병합 후 응답
```


#### 인기 상품 조회 플로우 (스케줄러 + 캐시)

```bash
PopularProductScheduler (1분마다 실행)

product:view_ranking 에서 ZREVRANGE로 Top N ID 조회

DB에서 해당 상품들을 Batch 조회

조회수 기반으로 재정렬

popular:products:top 에 List<RepProductDto> 형태로 저장 (TTL 5분)
```



#### 설정 (application.yml 일부)

```bash
popular-products:
top-count: 10 # 상위 N개
cache-ttl-minutes: 5 # TTL 5분
refresh-cron: "0 * * * * *" # 1분마다 갱신
```


#### 캐싱 전략 의사결정

| 전략 | 설명 | 장점 | 단점 | 선택 |
|------|------|------|------|------|
| 실시간 조회 | 매 요청마다 Redis+DB 조회 | 항상 최신 | 부하 ↑, 응답 시간 ↑ | ❌ |
| 캐싱 + TTL | API 호출 시 캐시 우선, TTL로 만료 | 부하 ↓ | TTL 동안 데이터 늦게 변함 | △ |
| 캐싱 + 스케줄러 | 주기적으로 인기 목록을 미리 계산 | 부하 최소, 일정한 최신성 | 스케줄러 관리 필요 | ✅ |

- **최종**: 스케줄러 1분 주기 + TTL 5분 (Refresh-Ahead)  
  - 정상 시: 최대 1분 지연  
  - 스케줄러 장애 시: 캐시로 최대 5분 버팀  

#### 성능 결과 (캐싱 전/후)

| 항목 | 캐싱 전 | 캐싱 후 | 변화 |
|------|---------|---------|------|
| 평균 응답 시간 | 65ms | 14ms | 78.5% 감소 |
| 처리량 | 725.7 req/sec | 1008.1 req/sec | 38.9% 증가 |
| DB 쿼리 (1000 요청) | 1000회 | 1회 | 99.9% 감소 |
| 응답 시간 편차 | 67.37 | 28.16 | 58% 감소 |
| 최대 응답 시간 | 455ms | 261ms | 42% 감소 |

</details>

---

### 2️⃣ 실시간 입찰 – Redis 분산락 + Kafka

<details>
<summary><b>다중 사용자 입찰에서도 단일 최고가 보장</b></summary>

#### 목표
- 동시에 여러 사용자가 같은 상품에 입찰해도  
  - 중복 낙찰 X  
  - 최고가 역전 X  
  - 상태 불일치 X  

#### 기술 선택

- **Redis 분산락 (Distributed Lock)**  
  - Key: `auction:{auctionId}`  
  - 다중 인스턴스 환경에서도 동일 규칙 적용  
- **Kafka 이벤트**  
  - 입찰 발생/결제 결과를 비동기로 처리  

#### 입찰 처리 플로우

사용자 입찰 요청

AuctionController.placeBid()

@DistributedLock("auction:{auctionId}") 으로 락 획득

BidService.placeBid()

입찰 금액 검증

현재 최고가와 비교

Bid 상태를 PENDING 으로 저장

BidPlacedEvent 발행 (Kafka)

즉시 응답 반환



#### 결제(HOLD) 및 상태 반영 플로우

BidPlacedEvent 수신 (WalletService)

잔액 검증 및 금액 HOLD

WalletHoldResultEvent 발행

AuctionService 가 이벤트 수신

Bid 상태를 HELD / FAIL 로 변경 (idempotent)


#### 핵심 코드 예시 (분산락)

```bash
@DistributedLock(
key = "auction:{auctionId}",
waitTime = 5,
leaseTime = 3
)
public BidResult placeBid(...) {
// 락을 트랜잭션보다 먼저 획득
// 임계 영역 최소화
// TTL 기반으로 비정상 종료 대비
}
```


#### 성능 비교 (이벤트 기반 전/후)

| 항목 | 변경 전 | 변경 후 | 효과 |
|------|---------|---------|------|
| 평균 응답 시간 | 10,491ms | 3,819ms | 64% 감소 |
| 최소 응답 시간 | 1,055ms | 2ms | 대기 제거 |
| 최대 응답 시간 | 19,672ms | 8,312ms | 꼬리 지연 감소 |
| 표준편차 | 5,155.56 | 2,522.64 | 응답 안정성 상승 |
| 처리량 | 48.1/sec | 107.5/sec | 123% 증가 |

</details>

---

### 3️⃣ 결제 – 비관락 기반 정합성 보장

<details>
<summary><b>금전 도메인: 성능보다 정합성 우선</b></summary>

#### 목표
- 여러 동시 요청에도 지갑 잔액이 절대 깨지지 않도록 보장  

#### 비관락 적용

```bash
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Wallet> findByUserId(String userId);
```


- 같은 유저 지갑 row에 대한 쓰기 요청 직렬화  
- 충돌이 잦은 도메인이라 낙관락보다 비관락 선택  

#### 동시성 테스트 (2,000 요청 / 100 쓰레드)

| 항목 | 락 미적용 | 락 적용 |
|------|-----------|---------|
| 기대 차감 | 2,000 | 2,000 |
| 실제 차감 | 205 | 2,000 |
| 정합성 | 깨짐 | 정상 |

- 락 미적용: 일부 요청만 반영 → **갱신 유실 발생**  
- 락 적용: 2,000건 모두 반영 → **정합성 확보**  

#### 트레이드오프

- 정합성은 확보했지만  
  - 평균 응답 시간 증가 (135ms → 223ms)  
  - 처리량 감소 (381.0 → 237.9 req/sec)  
- 추후 **원자적 UPDATE + 낙관락 + 재시도** 전략과 비교해 최적 선택 예정  

</details>

---

### 4️⃣ SSE 채팅 & 알림

<details>
<summary><b>Polling → WebSocket → SSE 최종 선택</b></summary>

#### 채팅 실시간성 요구

- 상품 등록자와 유저 간 채팅  
- 실시간 알림 필요  

#### 기술 선택 과정

| 방식 | 장점 | 단점 | 결론 |
|------|------|------|------|
| Polling | 구현 간단 | 실시간성 부족, 서버 부하 ↑ | ❌ |
| WebSocket | 양방향, 완전 실시간 | 별도 서버/포트, 운영 복잡 | △ |
| SSE | HTTP 기반, 구현 쉬움 | 단방향 통신 | ✅ |

- **SSE(Server-Sent Events)** 최종 채택  
  - HTTP 기반 인프라 그대로 활용 가능  
  - 자동 재연결 등 기본 기능 지원  
  - 채팅/알림에는 단방향 구조도 충분  

#### 알림 서비스 – Slack 연동 비동기화

- Kafka 이벤트 기반 알림 생성  
- 기존: Slack 발송을 동기 처리 → 응답 느림  
- 개선: Slack 발송을 비동기 + 이벤트 기반으로 변경  

**JMeter 결과**

| 항목 | 변경 전 | 변경 후 | 효과 |
|------|---------|---------|------|
| 평균 응답 시간 | 3,923ms | 138ms | 96.5% 감소 |
| 최소/최대 | 224 / 15,675ms | 2 / 1,669ms | 전구간 개선 |
| 표준편차 | 2,573.01 | 256.10 | 90% 감소 |
| 처리량 | 24.0/sec | 463.6/sec | 약 19배 증가 |

</details>

---

### 5️⃣ 비즈니스 규칙 강화 – 주소/리뷰

<details>
<summary><b>기본 주소 단일성과 거래 기반 리뷰 작성</b></summary>

#### 사용자 주소

- 여러 주소 등록 가능  
- **항상 기본 주소는 하나만 존재**  
- 기본 주소 삭제/해제 불가  

핵심 로직 요약:

- 새 기본 주소 설정 시, 기존 기본 주소의 `isDefault = false`  
- 기본 주소 삭제 시 예외 발생  
- 기본 주소를 그냥 `false`로 바꾸는 것도 허용하지 않고,  
  다른 주소를 기본으로 설정하는 방식으로만 변경 가능  
- `(주소 + 우편번호 + 상세주소)` 조합 중복 방지  

#### 리뷰 – 거래 참여자 검증

- seller/buyer만 서로에게 리뷰 작성 가능  
- 동일 `auctionId` 에 리뷰 1개만 허용  
- 본인에게 본인이 리뷰 작성 불가  
- auction-service 와는 Kafka Request–Response 패턴으로 통신  

핵심 플로우:

리뷰 요청
↓
사용자 존재 및 본인 리뷰 여부 검증
↓
해당 경매에 이미 리뷰 존재 여부 확인
↓
auction-service에 거래 검증 요청 (Kafka)
↓
응답으로 sellerId/buyerId/isValid 확인
↓
요청자가 seller/buyer 중 하나인지, 대상이 반대 역할인지 검증


</details>

---


## 🚀 실행 방법

### 📋 사전 요구사항

- **Docker & Docker Compose** 설치 필수
- **Java 17** 이상
- **최소 8GB RAM** 권장

---


### 🎯 실행 단계

#### 1️⃣ 인프라 서비스 시작

먼저 인프라(PostgreSQL, Redis, Kafka, Prometheus, Grafana)를 구동합니다:

```bash
docker-compose.infrastructure.yml 실행
docker-compose -f docker-compose.infrastructure.yml up -d
```


**실행되는 서비스:**

| 서비스 | 포트 | 설명 |
|--------|------|------|
| PostgreSQL | 5432 | 메인 데이터베이스 |
| Redis | 6379 | 캐시 & 세션 스토어 |
| Zookeeper | 2181 | Kafka 코디네이션 |
| Kafka | 9092 | 이벤트 스트리밍 |
| Prometheus | 19099 | 메트릭 수집 |
| Grafana | 19101 | 모니터링 대시보드 |

---

#### 2️⃣ 애플리케이션 서비스 시작

인프라가 정상 구동된 후 애플리케이션 서비스를 시작합니다:

```bash
docker-compose.yml 실행
docker-compose up -d
```


**실행되는 서비스:**

| 서비스 | 포트 | 설명 |
|--------|------|------|
| Eureka Server | 19090 | 서비스 디스커버리 |
| Gateway | 19091 | API 게이트웨이 |
| User Service | 19092 | 유저 관리 |
| Product Service | 19093 | 상품 관리 |
| Auction Service | 19094 | 경매 관리 |
| Payment Service | 19095 | 결제 처리 |
| Chat Service | 19096 | 채팅 (SSE) |
| Alert Service | 19097 | 알림 |
| Review Service | 19098 | 리뷰 관리 |

---

#### 3️⃣ 서비스 상태 확인

```bash
실행 중인 컨테이너 확인
docker-compose ps

전체 로그 확인
docker-compose logs -f

특정 서비스 로그 확인
docker-compose logs -f [service-name]
```

---

#### 4️⃣ 주요 접속 URL

<table>
  <tr>
    <th>서비스</th>
    <th>URL</th>
    <th>설명</th>
  </tr>
  <tr>
    <td><b>🌐 API Gateway</b></td>
    <td><code>http://localhost:19091</code></td>
    <td>모든 API 요청 진입점</td>
  </tr>
  <tr>
    <td><b>🔍 Eureka Dashboard</b></td>
    <td><code>http://localhost:19090</code></td>
    <td>서비스 디스커버리 대시보드</td>
  </tr>
  <tr>
    <td><b>📊 Grafana</b></td>
    <td><code>http://localhost:19101</code></td>
    <td>모니터링 대시보드<br/>(기본 계정: admin/admin)</td>
  </tr>
  <tr>
    <td><b>📈 Prometheus</b></td>
    <td><code>http://localhost:19099</code></td>
    <td>메트릭 수집 서버</td>
  </tr>
  <tr>
    <td><b>🔎 Zipkin</b></td>
    <td><code>http://localhost:9411</code></td>
    <td>분산 추적 대시보드</td>
  </tr>
</table>

---

#### 5️⃣ 서비스 종료

```bash
애플리케이션 서비스 종료
docker-compose down

인프라 서비스 종료
docker-compose -f docker-compose.infrastructure.yml down

볼륨까지 삭제 (데이터 초기화)
docker-compose down -v
docker-compose -f docker-compose.infrastructure.yml down -v
```

---



## 🐛 트러블 슈팅

<details>
<summary><b>1️⃣ Redis 캐싱 – LinkedHashMap 캐스팅 에러</b></summary>

- 증상: `ClassCastException: class java.util.LinkedHashMap cannot be cast to RepProductDto`  
- 원인: Redis에서 꺼낸 JSON을 역직렬화할 때 타입 정보가 없어 기본적으로 `LinkedHashMap`으로 변환됨  
- 해결: ObjectMapper에 타입 정보를 포함하도록 설정  

- 결과: Redis에서 꺼낸 객체가 `RepProductDto` 타입으로 정확히 역직렬화됨  

</details>

<details>
<summary><b>2️⃣ Kafka 이벤트 전환 시 Instant 직렬화 이슈</b></summary>

- 내부 API 호출 시에는 같은 JVM 내 Jackson 설정 공유 → 문제 없음  
- Kafka 이벤트로 바꾸면서 `Instant` 타입이 문자열 포맷/타임존 문제로 깨짐  
- 해결: 이벤트 전용 DTO에서 날짜를 `String`(ISO-8601)으로 변환해 전송  


</details>

<details>
<summary><b>3️⃣ 이벤트 구조 도입 후 성능 저하</b></summary>

- 이벤트만 추가하고 DB 조회는 그대로 두어서 오히려 성능 악화  
- 해결: User/Review 정보에 Redis 캐시 도입, 캐시 미스 때만 이벤트 기반 조회  

**결과(유저/리뷰)**

| 항목 | 변경 전 | 캐시 도입 후 |
|------|---------|--------------|
| 평균 응답 시간 | 74ms | 2ms |
| 처리량 | 678/sec | 981/sec |
| 표준편차 | 38.37 | 0.6 |

</details>

<details>
<summary><b>4️⃣ 채팅 대용량 조회 – 인덱스 미비로 인한 커넥션 풀 고갈</b></summary>

- 10만 채팅방, 200만 메시지 더미 데이터로 테스트 시 커넥션 풀 고갈  
- 원인: 조회 패턴에 맞는 인덱스 부재로 Full Scan 발생  
- 해결:  
  - 채팅방별 최신 채팅 조회용 인덱스  
  - 발신자/수신자 조건 조회용 복합 인덱스 추가  
- 결과: Error 0%, Throughput 318.1/sec 수준으로 안정화  

</details>

---

## 📊 성능 개선 결과



| 서비스 | 항목 | Before | After | 개선 효과 |
|--------|------|--------|-------|-----------|
| 상품 | 평균 응답 시간 | 65ms | 14ms | 78.5% 감소 |
|      | 처리량 | 725.7/s | 1008.1/s | 38.9% 증가 |
|      | DB 쿼리(1000요청) | 1000회 | 1회 | 99.9% 감소 |
| 입찰 | 평균 응답 시간 | 10,491ms | 3,819ms | 64% 감소 |
|      | 처리량 | 48.1/s | 107.5/s | 123% 증가 |
| 결제 | 평균 응답 시간 | 13ms | 2ms | 6배 개선 |
|      | 트래픽 | 26.63KB/s | 0.41KB/s | 98% 감소 |
| 알림 | 평균 응답 시간 | 3,923ms | 138ms | 96.5% 감소 |
|      | 처리량 | 24/s | 463.6/s | 19배 증가 |
| 유저/리뷰 | 평균 응답 시간 | 74ms | 2ms | 97% 감소 |
|      | 처리량 | 678/s | 981/s | 44.7% 증가 |

---

<div align="center">

**JavaAuction**  



</div>






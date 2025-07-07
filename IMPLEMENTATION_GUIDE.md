# TimeRangeChecker 기능 구현 상세 가이드

## 📋 목차
1. [개요](#개요)
2. [핵심 알고리즘](#핵심-알고리즘)
3. [아키텍처 설계](#아키텍처-설계)
4. [도메인 모델](#도메인-모델)
5. [비즈니스 로직](#비즈니스-로직)
6. [API 설계](#api-설계)
7. [데이터베이스 설계](#데이터베이스-설계)
8. [성능 최적화](#성능-최적화)

## 개요

### 프로젝트 목적
특정 시각이 주어진 시간 범위 내에 포함되는지 판단하는 웹 서비스 구현

### 핵심 요구사항
- **입력**: 특정 시각(0-23시) + 시간 범위(시작시각, 종료시각)
- **시간 형식**: 정수로 표현 (예: 6시 → 6)
- **범위 판단 규칙**: 시작시각 포함(>=), 종료시각 제외(<)
- **특별 케이스**: 시작시각 = 종료시각인 경우 포함
- **핵심 도전과제**: 자정을 넘나드는 시간대 처리 (예: 22시~5시)
- **제약사항**: 외부 라이브러리 사용 금지

## 핵심 알고리즘

### 1. 시간 순환성 처리 접근법

시간의 순환적 특성(23시 다음이 0시)을 고려한 3가지 케이스 분류:

#### 케이스 1: 일반적인 범위 (NORMAL)
```
예시: 9시~17시
조건: start <= target < end
로직: targetHour >= startHour && targetHour < endHour
```

#### 케이스 2: 자정 교차 범위 (MIDNIGHT_CROSSING)
```
예시: 22시~5시
조건: target >= start OR target < end
로직: targetHour >= startHour || targetHour < endHour
```

#### 케이스 3: 24시간 전체 (FULL_DAY)
```
예시: 0시~0시
조건: 항상 포함
로직: true
```

### 2. 핵심 구현 코드

```java
// 시간 범위 타입 판별
private TimeRangeType determineRangeType(Integer startHour, Integer endHour) {
    if (startHour.equals(endHour)) {
        return TimeRangeType.FULL_DAY;
    } else if (startHour > endHour) {
        return TimeRangeType.MIDNIGHT_CROSSING;
    } else {
        return TimeRangeType.NORMAL;
    }
}

// 시간 포함 여부 판단 - 핵심 로직
private boolean isTimeIncluded(Integer targetHour, Integer startHour, Integer endHour, TimeRangeType rangeType) {
    return switch (rangeType) {
        case FULL_DAY -> true;
        case MIDNIGHT_CROSSING -> targetHour >= startHour || targetHour < endHour;
        case NORMAL -> targetHour >= startHour && targetHour < endHour;
    };
}
```

### 3. 알고리즘 복잡도
- **시간 복잡도**: O(1) - 단순 비교 연산만 수행
- **공간 복잡도**: O(1) - 추가 메모리 사용 없음
- **외부 라이브러리**: 사용하지 않음 (순수 정수 연산)

## 아키텍처 설계

### 1. 레이어드 아키텍처

```
┌─────────────────────────────────────┐
│           API Layer                 │
│  - TimeCheckController              │
│  - StatisticsController             │
│  - GlobalExceptionHandler           │
└─────────────────────────────────────┘
                    │
┌─────────────────────────────────────┐
│        Application Layer            │
│  - TimeCheckService                 │
│  - StatisticsService                │
│  - DTO Classes                      │
└─────────────────────────────────────┘
                    │
┌─────────────────────────────────────┐
│         Domain Layer                │
│  - TimeCheckLog Entity              │
│  - TimeRangeStats Entity            │
│  - DailyStats Entity                │
│  - Repository Interfaces            │
└─────────────────────────────────────┘
                    │
┌─────────────────────────────────────┐
│      Infrastructure Layer          │
│  - JPA Repositories                 │
│  - Database Configuration           │
│  - External API Integration         │
└─────────────────────────────────────┘
```

### 2. 패키지 구조

```
src/main/java/junseop/timerangechecker/
├── api/                           # API 레이어
│   ├── controller/                # REST 컨트롤러
│   ├── ApiResponse.java           # 통일된 응답 형식
│   └── GlobalExceptionHandler.java # 전역 예외 처리
├── application/                   # 애플리케이션 레이어
│   ├── dto/                       # 데이터 전송 객체
│   └── service/                   # 비즈니스 로직
├── domain/                        # 도메인 레이어
│   ├── repository/                # 레포지토리 인터페이스
│   └── [Entity Classes]           # 도메인 엔티티
├── config/                        # 설정 클래스
├── exception/                     # 예외 클래스
└── TimeRangeCheckerApplication.java
```

## 도메인 모델

### 1. BaseEntity
모든 엔티티의 공통 필드를 관리하는 추상 클래스

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {
    private LocalDateTime createdAt;    // 생성 시간
    private LocalDateTime updatedAt;    // 수정 시간
    private Boolean isActive = true;    // 활성화 상태
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

**설계 원리**:
- **Soft Delete**: `isActive` 필드로 논리적 삭제 구현
- **Audit 기능**: 생성/수정 시간 자동 관리
- **DRY 원칙**: 공통 필드의 중복 제거

### 2. TimeCheckLog 엔티티
시간 체크 요청의 모든 로그를 저장

```java
@Entity
@Table(name = "time_check_logs")
public class TimeCheckLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer targetHour;     // 대상 시간
    
    @Column(nullable = false)
    private Integer startHour;      // 시작 시간
    
    @Column(nullable = false)
    private Integer endHour;        // 종료 시간
    
    @Column(nullable = false)
    private Boolean isIncluded;     // 포함 여부
    
    @Column(length = 45)
    private String clientIp;        // 클라이언트 IP
    
    @Column(length = 100)
    private String sessionId;       // 세션 ID
}
```

**설계 고려사항**:
- **로그 목적**: 모든 요청을 추적하여 사용 패턴 분석
- **성능**: 인덱스 전략 고려 (createdAt, sessionId)
- **개인정보**: IP 주소는 마스킹 처리 고려

### 3. TimeRangeStats 엔티티
시간 범위별 통계 정보 저장

```java
@Entity
@Table(name = "time_range_stats")
public class TimeRangeStats extends BaseEntity {
    @Column(nullable = false, unique = true, length = 10)
    private String rangeKey;        // "09-17" 형식
    
    @Column(nullable = false)
    private Integer requestCount;   // 요청 횟수
    
    @Column(nullable = false)
    private Integer matchCount;     // 매치 횟수
    
    @Column(nullable = false)
    private LocalDateTime lastAccessed; // 마지막 접근 시간
    
    // 비즈니스 메소드
    public void incrementRequest(Boolean isMatch) {
        this.requestCount++;
        if (isMatch) {
            this.matchCount++;
        }
        this.lastAccessed = LocalDateTime.now();
    }
    
    public double getMatchRate() {
        if (requestCount == 0) return 0.0;
        return (double) matchCount / requestCount * 100;
    }
}
```

**핵심 기능**:
- **실시간 집계**: 요청할 때마다 통계 업데이트
- **매치율 계산**: 비즈니스 로직을 엔티티 내부에 캡슐화
- **범위키 표준화**: "HH-HH" 형식으로 통일

### 4. DailyStats 엔티티
일별 사용 통계 저장

```java
@Entity
@Table(name = "daily_stats")
public class DailyStats extends BaseEntity {
    @Column(nullable = false, unique = true)
    private LocalDate date;         // 날짜
    
    @Column(nullable = false)
    private Integer totalRequests;  // 일별 총 요청수
    
    @Column(nullable = false)
    private Integer uniqueSessions; // 일별 유니크 세션수
    
    @Column(length = 10)
    private String popularRange;    // 인기 시간 범위
}
```

## 비즈니스 로직

### 1. TimeCheckService - 핵심 서비스

#### 메인 플로우
```java
@Transactional
public TimeCheckResponse checkTimeInRange(TimeCheckRequest request, HttpServletRequest httpRequest) {
    // 1. 입력 유효성 검증
    validateTimeInput(request);
    
    // 2. 시간 범위 타입 판별
    TimeRangeType rangeType = determineRangeType(request.getStartHour(), request.getEndHour());
    
    // 3. 포함 여부 판단 (핵심 로직)
    boolean isIncluded = isTimeIncluded(request.getTargetHour(), request.getStartHour(), 
                                        request.getEndHour(), rangeType);
    
    // 4. 요청 로깅 (비동기적)
    saveRequestLog(request, isIncluded, httpRequest);
    
    // 5. 통계 업데이트 (비동기적)
    statisticsService.updateStatistics(request.getStartHour(), request.getEndHour(), isIncluded, httpRequest);
    
    // 6. 응답 생성
    return TimeCheckResponse.of(request.getTargetHour(), request.getStartHour(), request.getEndHour(), 
                               isIncluded, rangeType.getValue());
}
```

#### 입력 유효성 검증
```java
private void validateTimeInput(TimeCheckRequest request) {
    // Null 체크
    if (request.getTargetHour() == null || request.getStartHour() == null || request.getEndHour() == null) {
        throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER);
    }
    
    // 범위 체크 (0-23)
    if (!isValidHour(request.getTargetHour()) || 
        !isValidHour(request.getStartHour()) || 
        !isValidHour(request.getEndHour())) {
        throw new BusinessException(ErrorCode.INVALID_HOUR_RANGE);
    }
}

private boolean isValidHour(Integer hour) {
    return hour != null && hour >= 0 && hour <= 23;
}
```

### 2. StatisticsService - 통계 서비스

#### 실시간 통계 업데이트
```java
@Transactional
public void updateStatistics(Integer startHour, Integer endHour, Boolean isIncluded, HttpServletRequest request) {
    String rangeKey = TimeRangeStats.createRangeKey(startHour, endHour);
    String sessionId = getSessionId(request);
    LocalDate today = LocalDate.now();
    
    // 시간 범위 통계 업데이트
    updateTimeRangeStats(rangeKey, isIncluded);
    
    // 일간 통계 업데이트
    updateDailyStats(today, rangeKey, sessionId);
}
```

#### 통계 조회 최적화
```java
@Transactional(readOnly = true)
public StatsResponse getOverallStats() {
    // 최근 30일 데이터만 조회하여 성능 최적화
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(30);
    
    // 병렬 처리로 성능 향상 가능
    Long totalRequests = getTotalRequestsInPeriod(startDate, endDate);
    Long uniqueSessions = getUniqueSessionsInPeriod(startDate, endDate);
    
    // TOP 10만 조회하여 응답 크기 제한
    List<RangeStatsDto> popularRanges = getPopularRanges(10);
    List<DailyStatsDto> dailyStats = getDailyStats(endDate.minusDays(7), endDate);
    
    return StatsResponse.builder()
                        .totalRequests(totalRequests)
                        .uniqueSessions(uniqueSessions)
                        .popularRanges(popularRanges)
                        .dailyStats(dailyStats)
                        .build();
}
```

## API 설계

### 1. RESTful API 설계 원칙

#### URL 구조
```
/api/v1/time-check     # 시간 체크 (POST)
/api/v1/statistics/    # 통계 관련
├── overall           # 전체 통계 (GET)
└── health           # 서비스 상태 (GET)
```

#### 통일된 응답 형식
```java
public class ApiResponse<T> {
    private final int code;        // HTTP 상태 코드
    private final String message;  // 일본어 메시지
    private final T data;          // 실제 데이터
    
    public static <T> ApiResponse<T> OK(T data) {
        return new ApiResponse<>(HttpStatus.OK, "リクエストは正常に処理されました。", data);
    }
}
```

### 2. 시간 체크 API

#### 요청 DTO
```java
@Schema(description = "時間チェック要求")
public class TimeCheckRequest {
    @NotNull(message = "対象時間は必須です。")
    @Min(value = 0, message = "時間は0から23の間で入力してください。")
    @Max(value = 23, message = "時間は0から23の間で入力してください。")
    private Integer targetHour;
    
    @NotNull(message = "開始時間は必須です。")
    @Min(value = 0, message = "時間は0から23の間で入力してください。")
    @Max(value = 23, message = "時間は0から23の間で入力してください。")
    private Integer startHour;
    
    @NotNull(message = "終了時間は必須です。")
    @Min(value = 0, message = "時間は0から23の間で入力してください。")
    @Max(value = 23, message = "時間は0から23の間で入力してください。")
    private Integer endHour;
}
```

#### 응답 DTO
```java
@Schema(description = "時間チェック応答")
public class TimeCheckResponse {
    private Integer targetHour;    // 대상 시간
    private Integer startHour;     // 시작 시간
    private Integer endHour;       // 종료 시간
    private Boolean isIncluded;    // 포함 여부
    private String rangeType;      // 범위 타입
    private String description;    // 설명 (일본어)
    
    // 응답 생성 팩토리 메소드
    public static TimeCheckResponse of(Integer targetHour, Integer startHour, Integer endHour, 
                                       Boolean isIncluded, String rangeType) {
        return TimeCheckResponse.builder()
                                .targetHour(targetHour)
                                .startHour(startHour)
                                .endHour(endHour)
                                .isIncluded(isIncluded)
                                .rangeType(rangeType)
                                .description(generateDescription(targetHour, startHour, endHour, isIncluded, rangeType))
                                .build();
    }
}
```

### 3. Swagger 문서화

#### 상세한 API 문서화
```java
@Operation(
    summary = "時間範囲チェック",
    description = "指定された時刻が時間範囲内に含まれるかどうかをチェックします。" +
                 "深夜を跨ぐ時間範囲（例：22時〜5時）も対応しています。"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "チェック成功"),
    @ApiResponse(responseCode = "400", description = "不正なリクエスト")
})
```

#### 예시 요청/응답
```json
// 요청 예시
{
  "targetHour": 14,
  "startHour": 9,
  "endHour": 17
}

// 응답 예시
{
  "code": 200,
  "message": "リクエストは正常に処理されました。",
  "data": {
    "targetHour": 14,
    "startHour": 9,
    "endHour": 17,
    "isIncluded": true,
    "rangeType": "NORMAL",
    "description": "対象時間14時は9時から17時の範囲内に含まれます。"
  }
}
```

## 데이터베이스 설계

### 1. 테이블 구조

#### time_check_logs 테이블
```sql
CREATE TABLE time_check_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_hour INTEGER NOT NULL,
    start_hour INTEGER NOT NULL,
    end_hour INTEGER NOT NULL,
    is_included BOOLEAN NOT NULL,
    client_ip VARCHAR(45),
    session_id VARCHAR(100),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    INDEX idx_created_at (created_at),
    INDEX idx_session_id (session_id),
    INDEX idx_range (start_hour, end_hour)
);
```

#### time_range_stats 테이블
```sql
CREATE TABLE time_range_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    range_key VARCHAR(10) NOT NULL UNIQUE,
    request_count INTEGER NOT NULL DEFAULT 0,
    match_count INTEGER NOT NULL DEFAULT 0,
    last_accessed DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    INDEX idx_request_count (request_count DESC),
    INDEX idx_last_accessed (last_accessed DESC)
);
```

#### daily_stats 테이블
```sql
CREATE TABLE daily_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    total_requests INTEGER NOT NULL DEFAULT 0,
    unique_sessions INTEGER NOT NULL DEFAULT 0,
    popular_range VARCHAR(10),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    INDEX idx_date (date DESC),
    INDEX idx_total_requests (total_requests DESC)
);
```

### 2. 인덱스 전략

#### 성능 최적화 인덱스
- **시간 기반 조회**: `created_at` 인덱스
- **세션 추적**: `session_id` 인덱스
- **범위 검색**: `start_hour, end_hour` 복합 인덱스
- **통계 정렬**: `request_count DESC` 인덱스

#### 쿼리 최적화 예시
```sql
-- 인기 시간 범위 조회 (인덱스 활용)
SELECT * FROM time_range_stats 
WHERE is_active = true 
ORDER BY request_count DESC 
LIMIT 10;

-- 특정 기간 요청수 집계 (인덱스 활용)
SELECT COUNT(*) FROM time_check_logs 
WHERE created_at BETWEEN ? AND ? 
AND is_active = true;
```

## 성능 최적화

### 1. 데이터베이스 최적화

#### 배치 처리 최적화
```java
// 통계 업데이트 시 배치 처리
@Transactional
public void updateStatisticsBatch(List<StatUpdateRequest> requests) {
    // 같은 범위키끼리 그룹화하여 한 번에 처리
    Map<String, List<StatUpdateRequest>> groupedRequests = 
        requests.stream().collect(Collectors.groupingBy(req -> 
            TimeRangeStats.createRangeKey(req.getStartHour(), req.getEndHour())));
    
    groupedRequests.forEach(this::updateRangeStatsBatch);
}
```

#### 읽기 최적화
```java
// 읽기 전용 트랜잭션으로 성능 향상
@Transactional(readOnly = true)
public StatsResponse getOverallStats() {
    // 필요한 데이터만 SELECT
    // JOIN 최소화
    // 페이징 처리
}
```

### 2. 애플리케이션 최적화

#### 예외 처리 최적화
```java
// 로그 저장 실패가 메인 기능에 영향을 주지 않도록
private void saveRequestLog(TimeCheckRequest request, boolean isIncluded, HttpServletRequest httpRequest) {
    try {
        // 로그 저장 로직
        timeCheckLogRepository.save(log);
    } catch (Exception e) {
        log.error("요청 로그 저장 실패", e);
        // 메인 기능 계속 진행
    }
}
```

#### 메모리 최적화
```java
// 임시 세션 저장소 (실제 운영시에는 Redis 사용 권장)
private final Set<String> todayProcessedSessions = new HashSet<>();

// 메모리 누수 방지를 위한 정기적 정리
@Scheduled(cron = "0 0 0 * * *") // 매일 자정
public void clearTodayProcessedSessions() {
    todayProcessedSessions.clear();
}
```

### 3. 확장성 고려사항

#### 향후 Redis 캐싱 적용
```java
// 캐시 적용 예시 (향후 구현)
@Cacheable(value = "timeRangeStats", key = "#rangeKey")
public TimeRangeStats getTimeRangeStats(String rangeKey) {
    return timeRangeStatsRepository.findByRangeKeyAndIsActiveTrue(rangeKey)
                                   .orElse(null);
}

@CacheEvict(value = "timeRangeStats", key = "#rangeKey")
public void updateTimeRangeStats(String rangeKey, Boolean isIncluded) {
    // 통계 업데이트 후 캐시 무효화
}
```

#### 비동기 처리 고려
```java
// 향후 비동기 처리 적용 예시
@Async
public CompletableFuture<Void> updateStatisticsAsync(Integer startHour, Integer endHour, 
                                                    Boolean isIncluded, HttpServletRequest request) {
    updateStatistics(startHour, endHour, isIncluded, request);
    return CompletableFuture.completedFuture(null);
}
```

## 테스트 전략

### 1. 핵심 로직 테스트
```java
@Test
void 일반_시간_범위_테스트() {
    // Given
    TimeCheckRequest request = TimeCheckRequest.builder()
                                              .targetHour(14)
                                              .startHour(9)
                                              .endHour(17)
                                              .build();
    
    // When
    TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);
    
    // Then
    assertThat(response.getIsIncluded()).isTrue();
    assertThat(response.getRangeType()).isEqualTo("NORMAL");
}

@Test
void 자정_교차_시간_범위_테스트() {
    // Given
    TimeCheckRequest request = TimeCheckRequest.builder()
                                              .targetHour(2)
                                              .startHour(22)
                                              .endHour(5)
                                              .build();
    
    // When
    TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);
    
    // Then
    assertThat(response.getIsIncluded()).isTrue();
    assertThat(response.getRangeType()).isEqualTo("MIDNIGHT_CROSSING");
}
```

### 2. 엣지 케이스 테스트
```java
@Test
void 경계값_테스트() {
    // 시작 시간과 같은 경우 (포함)
    // 종료 시간과 같은 경우 (제외)
    // 0시와 23시 경계 테스트
    // 24시간 전체 범위 테스트
}
```

이 문서는 TimeRangeChecker 프로젝트의 모든 구현 세부사항을 포함하고 있습니다. 각 섹션은 실제 구현된 코드와 설계 결정의 근거를 상세히 설명하고 있어, 향후 유지보수나 확장 시 참고자료로 활용할 수 있습니다. 
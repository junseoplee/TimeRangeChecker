# TimeRangeChecker 🕐

**풀스택 시간 범위 체크 웹 애플리케이션**

특정 시간이 주어진 시간 범위에 포함되는지 확인하는 웹 서비스입니다. 자정을 넘나드는 시간 범위와 24시간 전체 범위도 지원합니다.

## 🏗️ 프로젝트 구조 (모노레포)

```
TimeRangeChecker/
├── backend/          # Spring Boot API 서버
│   ├── src/
│   ├── build.gradle
│   ├── Dockerfile
│   └── docker-compose.yml    # 백엔드 전용 (Spring Boot + Redis)
├── frontend/         # Vue.js 3 웹 애플리케이션
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   ├── Dockerfile.dev
│   └── docker-compose.yml    # 프론트엔드 전용 (Vue.js 개발 서버)
└── README.md
```

## 🛠️ 기술 스택

### 백엔드 (Backend)
- **Java 17** + **Spring Boot 3.x**
- **Spring Data JPA** + **H2/MySQL** 
- **Redis** 캐싱 시스템 (30분 TTL)
- **Gradle** 빌드 도구
- **Docker** 컨테이너화

### 프론트엔드 (Frontend)
- **Vue.js 3** + **TypeScript**
- **Vue Router** (SPA)
- **Pinia** (상태 관리)
- **Tailwind CSS** (예정)
- **Vite** 빌드 도구
- **Vercel** 배포 (예정)

## 🚀 빠른 시작

### 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

### 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
```

### Docker 실행 방법

#### 백엔드 개발/실행
```bash
# Spring Boot + Redis
cd backend
docker-compose up -d
```

#### 프론트엔드 개발 
```bash
# Vue.js 개발 서버 (백엔드는 별도 실행 필요)
cd frontend
docker-compose up -d
```

#### 개별 실행 (권장)
```bash
# 1. 백엔드 먼저 실행
cd backend && ./gradlew bootRun

# 2. 프론트엔드 실행 (새 터미널)
cd frontend && npm run dev
```

## 📊 주요 기능

### ⏰ 시간 체크 로직
- **일반 범위**: 9시~17시 (NORMAL)
- **자정 교차**: 22시~5시 (MIDNIGHT_CROSSING) 
- **24시간 전체**: 0시~0시 (FULL_DAY)

### 📈 통계 및 분석
- 요청 통계 수집
- 인기 시간 범위 분석
- 일별 사용량 집계
- Redis 캐싱으로 성능 최적화 (13.6배 향상)

### 🎯 REST API
```bash
# 시간 체크
POST /api/v1/time-check
{
  "targetHour": 15,
  "startHour": 14, 
  "endHour": 16
}

# 통계 조회
GET /api/v1/statistics/overall
```

## 🏃‍♂️ 성능 최적화

- **Redis 캐싱**: 13.6배 응답 속도 향상
- **캐시 TTL**: 시간체크 30분, 통계 10분
- **무효화 전략**: 자동 캐시 갱신

## 🌐 배포 환경

- **백엔드**: AWS/Railway + 도메인 연결
- **프론트엔드**: Vercel (예정)
- **데이터베이스**: MySQL (운영), H2 (개발)
- **캐시**: Redis

## 📋 개발 현황

- ✅ **백엔드 완료**: API, 캐싱, 통계 시스템
- ✅ **모노레포 구조**: 백엔드/프론트엔드 분리
- ✅ **Vue.js 초기화**: TypeScript + 기본 설정
- 🔄 **진행 중**: Tailwind CSS, API 클라이언트
- 📋 **예정**: UI 컴포넌트, Vercel 배포

## 🤝 기여하기

1. 이슈 생성 및 브랜치 작업
2. 커밋 메시지: `feat: 설명 (#이슈번호)`
3. PR 생성 및 리뷰

---

**⭐ 실시간 시간 범위 체크의 새로운 경험을 제공합니다!** 
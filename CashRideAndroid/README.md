# 🛴 CashRide Android

**안전운전 포인트 시스템을 통한 개인형 이동장치 안전 솔루션**

## 📱 Android 네이티브 앱 특징

### 🏗️ **아키텍처**
- **MVVM 패턴**: ViewModel + LiveData/Flow 사용
- **Room Database**: 로컬 데이터 저장
- **Repository 패턴**: 데이터 소스 추상화
- **Kotlin Coroutines**: 비동기 처리

### 🎨 **UI/UX**
- **Material Design 3**: 최신 디자인 가이드라인
- **Navigation Drawer**: 사이드바 네비게이션
- **ViewBinding**: 타입 안전한 뷰 바인딩
- **CardView**: 모던한 카드 인터페이스

### 🔧 **기술 스택**
- **언어**: Kotlin 100%
- **UI**: XML + ViewBinding
- **데이터베이스**: Room + SQLite
- **네트워크**: (향후 Retrofit 추가 예정)
- **의존성 주입**: (향후 Hilt 추가 예정)

## 📂 **프로젝트 구조**

```
app/src/main/java/com/cashride/
├── MainActivity.kt                 # 메인 액티비티
├── data/
│   ├── models/                     # 데이터 모델
│   │   ├── User.kt
│   │   ├── Ride.kt
│   │   └── Coupon.kt
│   ├── database/                   # Room 데이터베이스
│   │   ├── CashRideDatabase.kt
│   │   ├── UserDao.kt
│   │   └── RideDao.kt
│   └── repository/                 # 리포지토리
│       └── CashRideRepository.kt
└── ui/
    ├── viewmodel/                  # ViewModel
    │   └── CashRideViewModel.kt
    └── fragments/                  # Fragment들
        ├── MainFragment.kt
        ├── RidingFragment.kt
        ├── ReportFragment.kt
        ├── RewardsFragment.kt
        ├── ChallengeFragment.kt
        ├── ProfileFragment.kt
        └── StatsFragment.kt
```

## 🚀 **핵심 기능**

### 1️⃣ **안전운전 포인트 시스템**
- 실시간 안전점수 측정 (0~120점)
- 다양한 안전/위험 행동 감지
- 포인트 적립 및 사용

### 2️⃣ **연속 안전운전 보너스**
- 80점 이상 연속 운행 시 콤보 적용
- 최대 2.0배까지 포인트 배율 증가

### 3️⃣ **레벨 시스템**
- 5단계 사용자 레벨 (새내기~레전드)
- 레벨별 혜택 및 포인트 보너스

### 4️⃣ **포인트 상점**
- 다양한 기프티콘 구매
- 구매 내역 관리

### 5️⃣ **3단계 리포트 시스템**
- 점수 → 안전행동 → 포인트 순서로 표시
- 터치 기반 인터랙션
- 시각적 애니메이션

## 📊 **점수 계산 공식**

```kotlin
정규화점수 = min(안전점수, 100) / 100
보너스점수 = max(0, 안전점수 - 100) / 20
기본포인트 = (정규화점수² + 보너스점수) × 0.2 × 100
거리포인트 = 기본포인트 × 주행거리 × 0.5
액션보너스 = 긍정적행동수 × 2
콤보배율 = 1.0 + (연속횟수 × 0.05), 최대 2.0배
최종포인트 = (거리포인트 + 액션보너스) × 콤보배율 × 레벨보너스
```

## 🎮 **사용 방법**

### 1. 앱 실행
- 사용자 자동 초기화
- 메인 화면에서 현재 상태 확인

### 2. 운행 시작
- "🚀 운행 시작" 버튼 클릭
- 실시간 통계 표시 (속도, 거리, 점수, 시간)

### 3. 테스트 액션
- ✅ **보너스**: 완벽한 정지, 안전구간 서행, 신호준수
- ⚠️ **패널티**: Zone 과속, 폰 사용, 과속

### 4. 운행 종료
- "⏹ 운행 종료" 버튼 클릭
- 3단계 리포트 확인

### 5. 포인트 상점
- 사이드바 → "포인트 상점" 메뉴
- 기프티콘 구매 및 사용

## 🔧 **빌드 및 실행**

### 요구사항
- **Android Studio**: Arctic Fox 이상
- **Kotlin**: 1.9.10
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### 빌드 명령
```bash
# 프로젝트 클론 후
cd CashRideAndroid

# Gradle 빌드
./gradlew build

# 디버그 APK 생성
./gradlew assembleDebug

# 설치 및 실행
./gradlew installDebug
```

## 🆕 **웹앱 대비 개선사항**

### 🚀 **성능**
- 네이티브 성능으로 부드러운 애니메이션
- Room 데이터베이스로 빠른 로컬 저장
- 메모리 효율적인 코틀린 코루틴

### 📱 **모바일 최적화**
- 터치 인터페이스 최적화
- 안드로이드 네비게이션 패턴
- 백그라운드 처리 지원

### 🔐 **안정성**
- 타입 안전한 코틀린
- Room의 컴파일 타임 검증
- 생명주기 인식 컴포넌트

### 🎨 **디자인**
- Material Design 가이드라인 준수
- 일관된 UI/UX
- 접근성 지원

## 🔮 **향후 개발 계획**

### Phase 1: 기본 완성 ✅
- [x] 핵심 기능 구현
- [x] 데이터베이스 설계
- [x] UI/UX 구성

### Phase 2: 확장 기능
- [ ] 실제 GPS 연동
- [ ] 백엔드 서버 연동
- [ ] Push 알림
- [ ] 소셜 기능 (친구, 랭킹)

### Phase 3: 고도화
- [ ] AI 기반 위험 예측
- [ ] 실시간 교통 정보 연동
- [ ] 기업용 대시보드
- [ ] 공유 킥보드 업체 연동

---

**🎯 안전한 마이크로 모빌리티 생태계를 위한 혁신적 솔루션**

*Made with ❤️ by CashRide Team*

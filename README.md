# Mini Kit Album Prototype (Android)

뮤즈라이브(MuseLive) 지원을 위해 제작한 토이 프로젝트입니다.
미니키트 앨범을 “등록 → 라이브러리 탐색 → 스트리밍/다운로드 → 오프라인 재생”까지 Kit Alubm의 핵심 기능을 다루려고 집중했습니다.

핵심 목표: “Album 등록(코드/스캔) → Featured/Library 탐색(필터/검색) → 앨범 상세 (Tracks/Music/Photos/Videos) → 스트리밍/오프라인 재생”

------

## Tech Stack

- Kotlin
- Jetpack Compose (UI)
- Coroutines / Flow
- Hilt
- Room (Local DB): 캐싱, 모킹 리모트 서버
- Network: 서버 데이터를 json asset으로 활용하여 Mocking
- Media: Jetpack Media3(ExoPlayer) (스트리밍/로컬 재생)
- Image: Coil
- Camera/Scan: CameraX + ML Kit Barcode/QR Scanning

## Architecture

<img width="436" height="248" alt="image" src="https://github.com/user-attachments/assets/fda077f8-7ca5-49af-aaf0-c68b9659ccb0" />


------

## Screens

<img width="682" height="1024" alt="image" src="https://github.com/user-attachments/assets/7482334a-8329-4c45-8620-cd5cfbe1ae2a" />


#### 1) HomeScreen

- 홈 진입 시 상/하 스크롤로 전체 화면(히어로 영역/정보 영역) 노출
- Featured Albums 프리뷰 영역 제공


#### 2) RegisterScreen

- Code 등록: 코드 입력으로 등록 플로우 진입/취소(뒤로가기)
- Scan 등록: Tap to Start → 권한 허용 → 카메라 시작 → 스캔 인식 → 등록 완료 Toast

#### 3) LibraryScreen (View All)
- Featured Albums에서 View All 진입
- 필터 기능(예: Downloaded / Not Downloaded / Artist / Sort)
- 검색 기능(앨범명/아티스트 등)

#### 4) AlbumDetailScreen

- 탭 구성: Tracks / Music / Photos / Videos
- Tracks 탭에서 스트리밍 재생/일시정지
- 다운로드 여부에 따라 UX 분기:
- 미다운로드 앨범: 스트리밍 중심 + 다운로드 액션
- 다운로드 앨범: 오프라인 재생 가능(네트워크 OFF에서도)


### Demo Videos

#### Video 1) Home → Register (Code/Scan) → 등록 완료

https://github.com/user-attachments/assets/94abfed0-1796-4ef8-a13a-9e4afd3cb3da

1. 홈 화면 상/하 스크롤로 전체 화면 구성 노출
2. Register 화면 클릭
 - Code 등록 진입 후 뒤로 가기
 - Scan 클릭 → Tap to Start
 - 권한 허용 후 카메라 시작
 - 스캔 성공 → 등록 완료 Toast 확인



###  Flow2

![two](https://github.com/user-attachments/assets/1f83cc61-3b45-4e17-a9ad-89a6350934b4)


1. 여러 바코드를 한 번에 인식
2. 전송 성공 시 SENT
3. 실패 시 FAILED로 상태 반영 및 3회 재시도
4. Job Detail에서 클립보드에 Id 복사


------

## Data Model

```kotlin
data class Job(
    val id: String,
    val barcode: String,
    val time: LocalDateTime,
    val status: JobStatus,
    val errorText: String? = null,
    val retryCount: Int = 0
)
```

Status
- PENDING: 로컬 저장 완료, 전송 대기
- SENT: 전송 성공
- FAILED: 전송 실패(에러/재시도 카운트 기록)

```kotlin
sealed interface RemoteResult<out T> {
    data class Success<T>(val data: T) : RemoteResult<T>
    data class HttpError(val code: Int, val body: String?) : RemoteResult<Nothing>
    data class Offline(val e: IOException) : RemoteResult<Nothing>
    data class Unknown(val t: Throwable) : RemoteResult<Nothing>
}
```

물류센터/현장 환경에서의 다양한 네트워크 상태를 처리하기 위해 고안했습니다.

Success(성공), HttpError(서버에러), Offline(네트워크 끊김), Unknown(기타)를 정의하여 ViewModel에서 사용자에게 작업의 결과를 보여줍니다.

JobViewmodel.kt

```kotlin
when (remoteResult) {
    is RemoteResult.Success -> {
        _uiResult.value = UiResult.Idle
    }
    is RemoteResult.HttpError -> {
        _uiResult.value = UiResult.Fail(remoteResult.body ?: "알 수 없는 이유로 실패하였습니다.")
    }
    is RemoteResult.Offline -> {
        _uiResult.value = UiResult.Fail("네트워크를 확인해주세요.")
    }
    is RemoteResult.Unknown -> {
        _uiResult.value = UiResult.Fail(remoteResult.t.message ?: "알 수 없는 이유로 실패하였습니다.")
    }
}
```

Screen

```kotlin
LaunchedEffect(uiResult) {
    when (uiResult) {
        is UiResult.Fail -> {
          Log.i(JobViewModel.TAG, "Fail: ${(uiResult as UiResult.Fail).msg}")
          Toast.makeText(appContext, "실패했습니다.", Toast.LENGTH_SHORT).show()
        }
        else -> {  }
    }
}
```



------

#### Notes
>본 프로젝트는 “바코드 → 실제 물류 도메인 매핑(상품 조회, 주문 조회 등)”을 포함하지 않습니다.
이는 WMS/OMS 조회 및 프로세스 규칙이 필요한 별도 도메인 영역이며, 본 프로토타입은 오프라인 큐잉/동기화/정합성 흐름 검증에 집중했습니다.



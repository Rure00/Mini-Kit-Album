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

<img width="326" height="241" alt="architecture" src="https://github.com/user-attachments/assets/f95f99eb-5e33-40f7-bff4-c3b0b3baa66b" />



------

## Screens

<img width="750" height="750" alt="wire_frame" src="https://github.com/user-attachments/assets/5a5d8ec1-554c-46c3-94e3-fd5b8a3d3f50" />



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


#### Video 2) Featured Albums → View All → Filter/Search

https://github.com/user-attachments/assets/3ebe3745-f2a3-4155-827b-240c6bcb7e39

1. 홈에서 아래로 내려 Featured Albums 노출 (3개 정도 미리 등록)
2. View All 클릭 → Library 화면 진입
3. 필터 기능 사용
4. 검색 기능 사용



#### Video 3) Album Detail → Streaming → Offline Playback

https://github.com/user-attachments/assets/145edcee-b2fc-4b9f-877f-3cb39a4d1c06

1. Library에서 다운로드 안 된 앨범 진입
2. 탭(Track/Music/Photos/Videos) 구성 확인
3. Track에서 스트리밍 재생 후 정지
4. 다운로드된 앨범으로 이동 → 데이터 끄기
5. 저장된 음원 오프라인 재생


------

## Data Model

```kotlin
data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val releaseDate: String,
    val description: String,
    val coverUrl: String,
    val tracks: List<Track>,
    val images: List<String>? = null,
    val videos: List<String>? = null,
)

data class Track(
    val id: String,
    val albumId: String,
    val title: String,
    val uri: String,
    val durationSec: Int,
    val downloaded: Boolean,
)
```



LocalRepositoryImpl.kt (일부)

```kotlin
private val downloadedTrackFlow = downloadDataSource.observerTracks().map { list ->
        list.associateBy { it.id }
    }.stateIn(applicationScope, WhileSubscribed(5000), emptyMap())

private val cachedTrackRawFlow = combine(downloadedTrackFlow,localCacheDataSource.observerTracks() ) { down, raw ->
    raw.associate {
        it.id to it.toTrack(down.containsKey(it.id))
    }
}.stateIn(applicationScope, WhileSubscribed(5000), emptyMap())

private val cachedAlbumRawFlow = combine(localCacheDataSource.observeAlbums(), cachedTrackRawFlow) { albumRaws, trackMap ->
    albumRaws.map { raw ->
        val tracksForAlbum = raw.tracksId.mapNotNull { trackMap[it] }
        raw.toAlbum(tracks = tracksForAlbum)
    }
}.stateIn(applicationScope, WhileSubscribed(5000), emptyList())
```

- 이 프로젝트에서 다운로드 상태(downloadDataSource) 와 앨범/트랙 메타(localCacheDataSource) 가 서로 다른 소스에 있어서, 화면마다 따로 조합하면 정합성 깨짐/로직 중복/깜빡임이 생길 수 있었습니다.

- 그래서 LocalRepository를 @Singleton으로 DI하고, 여기서만 combine으로 두 소스를 합쳐 Track(downloaded) / Album(tracks) 최종 모델을 단일 Flow(SSOT) 로 만든다.

- stateIn로 결과를 캐싱해서 StateFlow로 전환하여, 앱 전체에서 동일한 스트림을 공유하고 중복 연산/중복 구독을 줄였다.



------

#### Notes
> 본 프로젝트는 실제 앨범 도메인을 단순화한 프로토타입입니다.
목표는 “등록 → 탐색 → 재생 → 다운로드 → 오프라인 재생”의 사용자 플로우와 상태 전이를 명확하게 보여주는 것입니다.



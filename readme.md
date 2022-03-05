# Growing
## 소개
미라클 모닝과 동기부여를 도와주는 자기계발 모바일 앱 서비스입니다.

|미라클 모닝|동기부여|
|:----:|:----:|
|![ezgif com-gif-maker](https://user-images.githubusercontent.com/48471292/155334149-59761a9a-a257-49fb-b920-bc877dc51848.gif)|![ezgif com-gif-maker](https://user-images.githubusercontent.com/48471292/156901563-84ea5feb-bc19-4aff-bc32-c3381f4fa697.gif)|

- 동기
  - 실제로 취업 준비를 하면서 동기부여에 도움을 줄 수 있는 앱이 있으면 어떨까 생각했습니다.
  - MVVM 패턴을 학습해서 적용하고 싶었습니다.


- 목표: 스토어에 배포해서 작게나마 서비스를 해보려고 합니다.

### 특징
MVVM 패턴 적용, 커스텀 캘린더 구현, 야간 모드 지원, 리팩토링

### 기여도
개인 프로젝트

<br>

## 주요 서비스
✔ 미라클 모닝: 일어난 시간을 달력에 스탬프처럼 기록할 수 있습니다.

✔ 동기부여: 유튜브의 동기부여 영상들을 주제별로 분류해서 보여줍니다.
- [ ] 함께 성장하기: 게시판에 자신의 다짐이나 해낸 일 등을 공유합니다. (기획 단계)

<br>

## 제작 기간
- 미라클 모닝: 2022-01-27 ~ 2022-02-1 (기획, 구현, UI, 리팩토링)
- 동기 부여: 2022-03-02 ~ 2022-03-06 (기획, 데이터 수집/분류, 구현, UI)
- 함께 성장:

<br>

## 주요 사용 기술
Kotlin, MVVM, Databinding, ViewModel, LiveData, Coroutine, Room, Custom Calendar


### 코루틴
<details>
    <summary>자세히</summary>

[CalendarViewModel.kt](app/src/main/java/com/eryuksa/growing/miracle_morning/calendar/CalendarViewModel.kt)  - 캘린더를 보여주는 프래그먼트의 뷰모델

#### 1. 날짜 리사이클러뷰에서 사용할 스탬프 정보를 리포지토리에서 가져옵니다.

```kotlin
init {
  setUpMiracleDateList()
}

private fun setUpMiracleDateList() {
    // ...

    // 일어난 시간을 담고 있는 스탬프 객체들을 Room에서 가져온다
    viewModelScope.launch {
        loadStamps()
        _isStampLoaded.value = true // 스탬프 로딩 완료
    }
}
```
- suspend 함수를 사용해서 스탬프 로딩을 끝마쳤을 때 데이터를 갱신합니다.

<br>

#### 2. month에 맞는 스탬프를 가져옵니다. (첫 주와 마지막 주에 보여줄 이전 달과 다음 달 정보를 함께 가져옵니다)

```kotlin
private suspend fun loadStamps() {
        // ...

        // 코루틴스코프로 하위 코루틴이 모두 끝나야 suspend 함수가 종료되도록 함
        coroutineScope {
            launch { loadPrevMonthTailStamps(prevStartDay, prevEndDay) }
            launch { loadCurrentMonthStamps() }
            launch { loadNextMonthHeadStamps(nextEndDay) }
        }
    }
```
- coroutineScope를 사용해서 하위 코루틴들이 데이터 로딩을 모두 마치면 suspend 함수를 종료시킵니다.

<br>

#### 3. 스탬프를 가져와서 해당 위치의 리스트에 일어난 시간을 설정합니다.
```kotlin
private suspend fun loadCurrentMonthStamps() {
    val stamps = calendarRepository.getMonthStamps(currentDateTime.millis)

    stamps.forEach { stamp ->
        val pos = baseCalendar.prevMonthTailOffset + stamp.dayOfMonth - 1
        miracleDateList[pos].wakeUpMinutes.value = stamp.wakeUpMinutes
    }
}
```
[CalendarRepository.kt](app/src/main/java/com/eryuksa/growing/miracle_morning/calendar/data/CalendarRepository.kt)
```kotlin
private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

suspend fun getMonthStamps(monthMillis: Long): List<MiracleStamp> {
    return  withContext(coroutineScope.coroutineContext) {
        return@withContext calendarDao.getMonthStamps(monthMillis)
    }
}
```
- withContext를 사용해서 IO로 컨텍스트를 변경하고 작업 결과를 반환합니다. 

</details>

<br>

## 트러블 슈팅

### 유튜브 전체 화면
<details>
<summary>자세히</summary>

- 문제 상황  
  전체 화면 모드에서 회전되었던 화면 방향이 전체 화면을 해제했을 때 되돌아오지 않음
- 해결 방법  
  - YouTubePlayer에 있는 fullScreenListener를 추가해서 전체 화면 여부에 따라 Activity.requestedOrientation을 변경  
  ```kotlin
  player.setOnFullscreenListener {
      requestedOrientation = if (it) {
          ActivityInfo.SCREEN_ORIENTATION_LOCKED
      } else {
            ActivityInfo.SCREEN_ORIENTATION_USER
    }

    isPlayerFullScreen = it
  }
  ```
  - isPlayerFullScreen: Boolean 변수를 선언해서 백버튼을 클릭했을 때 전체 화면 모드를 해제하고 화면 방향을 유저가 설정한 방향으로 세팅  
   ```kotlin
   override fun onBackPressed() {
        if (isPlayerFullScreen && youtubePlayer != null) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
            youtubePlayer!!.setFullscreen(false)
        } else{
            super.onBackPressed()
        }
    }
   ```
</details>

<br>

## TODO
- 미라클 모닝
  1. 시간 입력 UI 개선
- 동기부여
  1. 무한 스크롤
  2. 유튜브 목록 데이터 추가/정리
  3. 좋아요, 북마크, 공유하기  
- 함께 성장하기
  1. 회원 기능
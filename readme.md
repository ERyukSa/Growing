# Growing
## 소개
미라클 모닝을 비롯해서 동기부여를 도와주는 자기계발 모바일 앱 서비스입니다.

### 동기
- 직접 취업 준비를 하면서 동기부여에 도움을 줄 수 있는 앱이 있으면 어떨까 생각했습니다.
- MVVM 패턴을 학습하고 싶었습니다.
- learning by doing 방식으로 보다 재밌게 학습하고 싶었습니다.
- 혼자서 맨땅에 헤딩해보고 싶었습니다.

### 주요 서비스
✔ 미라클 모닝: 일어난 시간을 달력에 스탬프처럼 기록할 수 있습니다.

✔ 동기부여: 유튜브의 동기부여 영상들을 주제별로 분류해서 보여줍니다.

✔ Todo 리스트: 오늘 할 일을 기록하고 관리합니다.

|1. 미라클 모닝|2. 동기부여|
|:----:|:----:|
|![ezgif com-gif-maker](https://user-images.githubusercontent.com/48471292/155334149-59761a9a-a257-49fb-b920-bc877dc51848.gif)|![ezgif com-gif-maker](https://user-images.githubusercontent.com/48471292/156901563-84ea5feb-bc19-4aff-bc32-c3381f4fa697.gif)|
<br>

### 3. Todo 리스트
|애니메이션|추가|변경(button, swipe)|
|:----:|:----:|:----:|
|![animation](https://user-images.githubusercontent.com/48471292/172117244-d5d57119-9438-4279-9514-62cecd24945f.gif)|![add](https://user-images.githubusercontent.com/48471292/172117378-5fb2a378-eea5-4963-8130-07dfb9bc18a2.gif)|![change status](https://user-images.githubusercontent.com/48471292/172117411-e1524f7d-48f9-4b56-8ccb-9bf04132f844.gif)|

변경(drag&drop)|삭제&되돌리기(button, swipe)|상태 유지(Room)|
|:----:|:----:|:----:|
|![drag drop](https://user-images.githubusercontent.com/48471292/172117489-6986f9c3-ed89-48e1-93ae-7f80b6a3215e.gif)|![remove](https://user-images.githubusercontent.com/48471292/172117587-dea9d6c9-e356-4032-af3b-89d9663abf6e.gif)|![유지](https://user-images.githubusercontent.com/48471292/172117653-9f4c32b7-ee2e-4e2d-a8d9-4396a0cfbbd1.gif)|
<br>

### 특징
AAC를 활용, MVVM 패턴 적용, 커스텀 캘린더, 야간 모드 지원, 리팩토링

### 기여도
개인 프로젝트

### 제작 기간
- 미라클 모닝: 2022-01-27 ~ 2022-02-18 (기획, 구현, UI, 리팩토링)
- 동기 부여: 2022-03-02 ~ 2022-03-06 (기획, 데이터 수집/분류, 구현, UI)
- Todo 리스트: 2022-04-25 ~ 2022-06-06

<br>

## 주요 사용 기술
Kotlin, MVVM, Databinding, (Shared)ViewModel, LiveData, Coroutine, Room, ListAdapter

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

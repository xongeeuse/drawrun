# 250208

1. KPT 회고

## KEEP
- 

## Problem
- 뭔가 명확하지 않고 정리가 안됨 복잡하다

## Try
- 하나씩 순서대로 처리하자

2. 오늘
- 지도에서 코스 저장 어떻게 연결되게 할 지 고민
- 최대한 `MapActivity` 안 건드리고 구현해야 할 것 같아서 `BottomSheetDialogFragment` 활용하기로 함 - 하단에서 올라오는 모달 형태
- 캡쳐에 코스 전체 그림 잘 들어가게 딸 수 있겠지?...
- 코스 저장할 때 이미지URL 보내려면 이미지 서버에 저장하는 API 분리해야 함

- 이제 흐름 좀 이해했다
    - `View`에서 이벤트 발생 시
    - 이벤트리스너가 `ViewModel`에 필요한 데이터 담아서 보내면
    - `ViewModel`에서 리퀘스트 객체에 담아서 `Repository`로
    - `Repository`에서 API에 리퀘스트 담아서 요청 보내고
    - 서버에서 응답 받아서
    - `ViewModel` 업데이트 시 `View`가 감지
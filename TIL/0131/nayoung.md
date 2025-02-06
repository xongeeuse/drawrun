# 2025.01.31(금) 로그인 기능 구현현

### KPT 작성

#### `Keep` : 현재 만족하고 있는 부분, 계속 이어갔으면 하는 부분

- Team

- 회의를 통해 프로젝트 방향성을 팀원들과 잡아가는 것

#### `Problem` : 불편하게 느끼는 부분, 개선이 필요하다고 생각되는 부분

- 다른 팀들의 진행상황에 따라 조급함을 느끼는 부분.

- 완성할 수 있을까에 대한 두려움..?

#### `Try` : problem에 대한 해결책, 다음 회고 때 판별 가능한 것, 당장 실행 가능한 것

- 우리팀의 속도에 맞춰 불안해하지 말것.

- 차근차근 천천히 팀원들과 성장하기.

---

####  로그인 기능 구현(`accessToken`, `refreshToken` 처리 방법)
    
    
- 프론트에서 LoginRequest 값으로 (userId, password) 데이터 서버에 전송하여 로그인 요청
    
- 백에서 LoginResponse값으로 (accessToken 은 응답데이터, refreshToken은 쿠키로 전달) 로그인 응답
    
    1. `accessToken`을 클라이언트에서 `SharedPreferences` 등에 저장하여 API 요청 시 사용해야 함.

    2. `refreshToken`이 쿠키에 저장되므로, **앱에서 직접 접근할 수 없음.** → 서버가 `HttpOnly` 쿠키를 사용한다면 앱에서 쿠키를 읽을 수 없음.

    3. `accessToken`이 만료되었을 때, **자동으로 `refreshToken`을 이용하여 새로운 `accessToken`을 받아야 함.**
    
- LoginResponse에선 서버에서 반환하는 `accessToken`만 포함(`refreshToken`은 쿠키에 저장되므로 필요없음).

---
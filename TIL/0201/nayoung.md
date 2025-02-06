# 2025.02.01(토) 로그인 기능 구현

### KPT 작성

#### `Keep` : 현재 만족하고 있는 부분, 계속 이어갔으면 하는 부분

- Team🔥

#### `Problem` : 불편하게 느끼는 부분, 개선이 필요하다고 생각되는 부분

- 개발기간이 얼마 안남은거같아서 불안함..?


#### `Try` : problem에 대한 해결책, 다음 회고 때 판별 가능한 것, 당장 실행 가능한 것

- 팀원들과 같이 고민하고 이슈 공유하는 것

- 포기하지 않기.

---

### 로그인 기능 구현하면서 배운 것.

#### `JWT(JSON Web Token)`란?

- `JWT`는 **사용자 인증과 권한 부여를 위한 토큰 기반 인증 방식**

- 주로 `accessToken`과 `refreshToken`으로 구성

<br>

|구분|**AccessToken**|**RefreshToken**|
|:--:|:------------:|:---------------:|
|목적|사용자 인증 및 API 요청|Access Token 재발급|
|유효 기간|짧음(5 ~ 15분)|김(1주 ~ 1개월)|
|저장 위치|프론트엔드<br>(LocalStorage, SecureStorage 등)|HttpOnly 쿠키<br>(서버 관리)|
|보안|HTTPS 필수, 탈취 위험 존재|JS 접근 불가(HttpOnly), CSRF 방지 강화|
|재발급 방법|새로 로그인 필요|자동 재발급 가능|


#### `HttpOnly`란?

- 정의 
    
    - HttpOnly 속성이 설정된 쿠키는 JavaScript에서 접근이 불가능

    - XSS 공격으로부터 안전하게 보호.

|**특징**|**설명**|
|:------:|:------:|
|보안 강화|JacvaScript 접근 차단 -> XSS 공격 방어|
|자동 전송|요청 시 자동으로 서버로 전송(프론트엔드 코드 불필요)|
|취약점|CSRF 공격에는 취약약할 수 있음(SameSite 속성으로 방어)|
|HTTPS 권장|Secure속성과 함께 사용 시 보안성 극대화|

#### `AccessToken & RefreshToken 동작 흐름`

1. 로그인 시

    - 서버가 `accessToken`(응답본문)과 `refreshToken`(HttpOnly쿠키)를 발급

    - 클라이언트는 `accessToken`을 저장하고 API 요청 시 사용

2. API 요청 시

    - 클라이언트가 `accessToken`을 **Authorization 헤더**에 담아 요청

    - 서버가 유효성 검사 후 정상 처리

3. Access Token 만료 시

    - 서버가 `401 Unauthorized` 반환

    - 클라이언트는 **쿠키에 있는 refreshToken**을 새 토큰 요청

    - 새로운 `accessToken`을 받아 저장 후 재요청

    - 보통은 AccessToken 만료 시 Refresh Token도 새로 발급하는 것을 권장하긴 한다.
        -> 주기적으로 갱신하여 탈취 위험 최소화(더 나은 보안성 제공)

4. 로그아웃 시

    - 서버에서 `refreshToken`을 삭제하고, 클라이언트의 `accessToken` 삭제

<br>

- 백에서 토큰 생성할 때 페이로드에 각 정보를 담아주는데 프론트에서 파싱해서 사용(ex. 백에서 던져준 accessToken을 파싱하여 사용자 정보 가져와서 사용)

---
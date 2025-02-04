
## 250201

1. KPT 회고
    #### Keep
        - Team


    #### Problem
        - 모바일-워치 간 서로 연동은 확실히 되는데 러닝 이후 데이터를 보내고 모바일에서 가공할지 고민해봐야할 것 같음

    #### Try
        - 갑자기 워치에서 워치 앱이 사라지는 문제를 겪음
        - 미공개앱으로 갑자기 처리되서 다시 공개앱으로 돌렸는데 이유를 모르겠음..
        - 잘 되다가 갑자기 그렇게 되서 식겁쳤다. 
        ```
        adb -s 172.30.1.29:45059 shell pm list packages -d
        adb -s 172.30.1.29:45059 shell pm set-hidden com.example.drawrun false
        adb -s 172.30.1.29:45059 shell pm clear com.example.drawrun

        ```


2. 오늘
- 공식문서가 죄다 영어인 mapbox 네비게이션에 붙었다가 다시 워치데이터로 돌아가니 천국같음 

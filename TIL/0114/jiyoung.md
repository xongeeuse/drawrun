## 250113

1. 아이디어 구체화 + 새로운 아이디어 추가 => 내일 오전 팀미팅 예정

2. `Kotlin` 기초 문법 마무리 및 심화 문법 강의 수강
    ```kotlin
    // lambda 이해하기

    fun extendString(name : String, age : Int) : String {
        val introduceMyself : String.(Int) -> String = { "I am ${this} and ${it} years old."}
        return name.introduceMyself(age)
}
    ```
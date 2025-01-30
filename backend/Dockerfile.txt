# Java 17 JRE 기반 컨테이너 사용
FROM eclipse-temurin:17-jre

# 빌드 시 사용할 JAR 파일 경로 설정
ARG JAR_FILE=build/libs/*.jar

# 컨테이너 내부에서 작업 디렉토리 설정
WORKDIR /app

# JAR 파일을 컨테이너 내부로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너가 사용할 포트 설정
EXPOSE 8080

# 컨테이너 실행 시 Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
# --- 1. 빌드(Build) 스테이지 ---
# Gradle과 JDK 21을 사용하여 애플리케이션을 빌드합니다.
FROM gradle:8.5-jdk21-jammy AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 전체 소스 코드를 컨테이너 안으로 복사
COPY . .

# Gradle을 사용하여 실행 가능한 JAR 파일을 빌드합니다.
# --no-daemon 옵션은 CI 환경에서 불필요한 Gradle 데몬이 실행되는 것을 방지합니다.
RUN gradle bootJar --no-daemon -x test

# --- 2. 런타임(Runtime) 스테이지 ---
# 실제 애플리케이션을 실행할 가벼운 JRE 21 이미지를 사용합니다.
FROM eclipse-temurin:21-jre-jammy

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 런타임 스테이지로 복사합니다.
# build/libs/ 디렉토리에 있는 .jar 파일을 app.jar로 이름을 변경하여 복사합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션이 8080 포트를 사용함을 명시합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 애플리케이션 JAR 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]

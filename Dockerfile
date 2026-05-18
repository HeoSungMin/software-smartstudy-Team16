# 1. 빌드를 위한 메이븐 내장 환경을 가져옵니다.
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# 2. 소스코드와 pom.xml만 복사합니다.
COPY pom.xml .
COPY src ./src

# 3. 메이븐으로 jar 파일을 빌드합니다.
RUN mvn clean package -DskipTests

# 4. 실행을 위한 가벼운 자바 환경으로 바꿉니다.
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 5. 빌드된 jar 파일만 쏙 빼옵니다.
COPY --from=build /app/target/studyapp-0.0.1-SNAPSHOT.jar app.jar

# 6. 실행합니다.
CMD ["java", "-jar", "app.jar"]

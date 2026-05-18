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

# 🌟 [이 부분을 수정] target 폴더 안의 모든 jar 파일을 실행 가능한 이름으로 가져옵니다.
COPY --from=build /app/target/*.jar app.jar

# 5. 실행합니다.
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

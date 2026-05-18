# 1. 자바 17 버전 환경을 빌려옵니다.
FROM openjdk:17-jdk-slim

# 2. 작업할 폴더를 만듭니다.
WORKDIR /app

# 3. 메이븐 빌드 도구와 소스코드를 서버로 복사합니다.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# 4. 리눅스 환경에서 실행할 수 있도록 권한을 주고 빌드합니다.
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# 5. 빌드가 완료되면 생성된 jar 파일을 실행합니다.
CMD ["java", "-jar", "target/studyapp-0.0.1-SNAPSHOT.jar"]
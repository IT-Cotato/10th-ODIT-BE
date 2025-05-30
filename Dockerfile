FROM openjdk:21
ARG JAR_FILE=build/libs/*.jar

# 두 ARG는 ec2 도커 컴포즈에 세팅된 변수 값을 따라가게 됨
ARG PROFILES
ARG ENV

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-DSpring.profiles.active=${PROFILES}", "-DServer.env=${ENV}", "-jar", "app.jar"]

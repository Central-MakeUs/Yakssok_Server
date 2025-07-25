FROM openjdk:17-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
ENV TZ=Asia/Seoul
CMD ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
ENV TZ=Asia/Seoul
CMD ["java", "-jar", "app.jar"]

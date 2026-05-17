FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
ENV TZ=Asia/Seoul
CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
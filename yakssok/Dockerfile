FROM gradle:8-jdk AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar

FROM openjdk:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar yakssok.jar
CMD ["java", "-jar", "yakssok.jar"]

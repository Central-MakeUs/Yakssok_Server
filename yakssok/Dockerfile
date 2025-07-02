FROM openjdk:17-jdk
WORKDIR /app
COPY build/libs/yakssok-0.0.1-SNAPSHOT.jar yakssok.jar
CMD ["java", "-jar", "yakssok.jar", "--spring.profiles.active=dev"]

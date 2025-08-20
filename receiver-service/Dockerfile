FROM openjdk:24-jdk-slim

WORKDIR /app

COPY target/receiver-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
FROM openjdk:21-slim
LABEL authors="GEOGREEN-PLATFORM"
COPY target/notification-server*.jar /notification-server.jar
ENTRYPOINT ["java", "-jar", "/notification-server.jar"]
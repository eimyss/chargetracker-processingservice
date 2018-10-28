FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/backend-processing-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=${stage}","-jar","/app.jar"]

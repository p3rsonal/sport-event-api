FROM openjdk:17-jdk-slim

WORKDIR /app

RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* \

COPY pom.xml .
COPY src src/
COPY settings.xml /root/.m2/settings.xml
RUN mvn package

LABEL authors="arthurdream"

COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
EXPOSE 8012

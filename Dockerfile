# #Stage 1
FROM maven:3.6.0-jdk-8-alpine AS build

WORKDIR /tmp

COPY pom.xml ./
#Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src/ ./src

RUN mvn -B -DskipTests clean package

# #Stage 2
# FROM openjdk:8-jdk-alpine
# WORKDIR /tmp
# COPY target/*.jar kwetter.jar
# ENTRYPOINT ["java", "-jar", "kwetter.jar"]


FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=build /tmp/target/*.jar ./kwetter.jar
ENTRYPOINT ["java", "-jar", "kwetter.jar"]

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseG1GC -verbose:gc -jar target/kwetter.jar

#VOLUME /tmp
#
#ARG JAR_FILE
#
#COPY ${JAR_FILE} app.jar
#
#ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "/app.jar"]
#Stage 1
FROM maven:3.6.0-jdk-8-alpine AS build

WORKDIR /tmp

COPY pom.xml ./

RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src/ ./src

RUN mvn -B -DskipTests clean package

#Stage 2
FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=build /tmp/target/*.jar ./kwetter.jar
ENTRYPOINT ["java", "-jar", "kwetter.jar"]

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseG1GC -verbose:gc -jar target/kwetter.jar
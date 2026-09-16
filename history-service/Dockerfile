FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q clean verify

FROM eclipse-temurin:21-jre-jammy

RUN groupadd --system spring && useradd --system --gid spring spring

USER spring:spring
WORKDIR /app

COPY --from=build /workspace/target/history-service-*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

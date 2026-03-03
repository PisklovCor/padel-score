FROM eclipse-temurin:17-jdk-alpine AS builder
RUN apk add --no-cache maven=3.9.11-r0
WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src
RUN mvn clean package -DskipTests -Dhadolint.skip=true





FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/padelscore-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

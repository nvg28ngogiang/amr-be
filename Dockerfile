# Stage 1: Build builder image with using jdk
FROM eclipse-temurin:11-jdk-alpine as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod 755 mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:resolve
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 ./mvnw package

# Stage 2: Build development image using jre to reduce image size
FROM eclipse-temurin:11-jre-alpine as development
WORKDIR /app
COPY --from=builder /app/target/amr-be-*.jar amr-be.jar
RUN apk add --no-cache curl
CMD ["java", "-jar", "-Dspring.profiles.active=dev", "amr-be.jar"]

# Stage 2: Build production image using jre to reduce image size
FROM eclipse-temurin:11-jre-alpine as production
WORKDIR /app
COPY --from=builder /app/target/amr-be-*.jar amr-be.jar
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "amr-be.jar"]
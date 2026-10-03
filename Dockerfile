FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew || true

# Production backend service container
FROM openjdk:17-jre-slim
WORKDIR /app
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=production
ENV ENVIRONMENT_MODE=PRODUCTION
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "paysetu-gateway.jar"]

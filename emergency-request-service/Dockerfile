# ==============================================================================
# FAIERS — Emergency Request Service Dockerfile
# Optimized for Amazon ECS (Fargate) & Local Docker Execution
# ==============================================================================

FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Install curl/wget for container health checks
RUN apk add --no-cache curl

# Create non-root system user and group for security compliance
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the executable Spring Boot JAR produced by Maven build
COPY /emergency-request-service/target/*.jar app.jar

# Ensure proper permissions
RUN chown -R appuser:appgroup /app

# Run as non-root user
USER appuser

# Expose microservice port
EXPOSE 8081

# Container-aware JVM memory options (respects ECS task CPU & RAM limits)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

# Healthcheck targeting Actuator endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Launch application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

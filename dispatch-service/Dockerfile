# ==============================================================================
# FAIERS — Dispatch Service Dockerfile
# Optimized for Amazon ECS (Fargate) & Local Docker Execution
# ==============================================================================

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root system user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy executable jar
COPY dispatch-service/target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8082

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"


HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

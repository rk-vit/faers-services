# ==============================================================================
# FAIERS — Central Database Migrations Dockerfile (Flyway)
# Used as an ECS Pre-deployment Migration Task or Init Container
# ==============================================================================

FROM flyway/flyway:10-alpine

# Copy all versioned SQL migrations into Flyway's default migration directory
COPY db-migrations/src/main/resources/db/migration /flyway/sql

# Set default connection retry parameters (waits for database to be ready)
ENV FLYWAY_CONNECT_RETRIES=15

# Default action is to run pending migrations
ENTRYPOINT ["flyway"]
CMD ["migrate"]

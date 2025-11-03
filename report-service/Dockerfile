# ==============================================================================
# Multi-stage Dockerfile for Report Service
# ==============================================================================
# CQRS-based reporting service with read-optimized queries
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Stage
# ------------------------------------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy dependencies
COPY pom.xml ../pom.xml
COPY common-lib ../common-lib

# Install parent POM first
RUN cd .. && mvn install -N -DskipTests --no-transfer-progress -B

# Copy service POM
COPY report-service/pom.xml ./pom.xml

# Build common-lib (required dependency)
RUN cd ../common-lib && mvn clean install -DskipTests --no-transfer-progress -B

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source
COPY report-service/src ./src

# Build
RUN mvn clean package -DskipTests --no-transfer-progress -B

# ------------------------------------------------------------------------------
# Stage 2: Runtime Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre

LABEL maintainer="ecommerce-team@example.com"
LABEL service="report-service"
LABEL version="1.0.0"
LABEL description="Reporting and Analytics Microservice"

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copy artifact
COPY --from=build /app/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8083/api/v1/reports/health || exit 1

# JVM Configuration
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]




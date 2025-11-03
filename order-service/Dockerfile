# ==============================================================================
# Multi-stage Dockerfile for Order Service
# ==============================================================================
# Optimized for production with security best practices and minimal image size
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Stage
# ------------------------------------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent and common dependencies
COPY pom.xml ../pom.xml
COPY common-lib ../common-lib

# Install parent POM first
RUN cd .. && mvn install -N -DskipTests --no-transfer-progress -B

# Copy service POM for dependency caching
COPY order-service/pom.xml ./pom.xml

# Build common-lib (required dependency)
RUN cd ../common-lib && mvn clean install -DskipTests --no-transfer-progress -B

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY order-service/src ./src

# Build application
RUN mvn clean package -DskipTests --no-transfer-progress -B

# ------------------------------------------------------------------------------
# Stage 2: Runtime Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre

LABEL maintainer="ecommerce-team@example.com"
LABEL service="order-service"
LABEL version="1.0.0"
LABEL description="Order Management Microservice with Kafka Integration"

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copy artifact from build stage
COPY --from=build /app/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/api/v1/orders/health || exit 1

# JVM Configuration for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]




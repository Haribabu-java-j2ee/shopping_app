# ==============================================================================
# Multi-stage Dockerfile for Auth Service
# ==============================================================================
# This Dockerfile uses multi-stage build to optimize image size and security.
# Stage 1: Build the application
# Stage 2: Create lightweight runtime image
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Stage
# ------------------------------------------------------------------------------
# Purpose: Compile Java code and package the application
# Base Image: Maven with JDK 17 for building (ARM64/x86_64 compatible)
# ------------------------------------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Set working directory for build process
WORKDIR /app

# Copy parent POM first for dependency resolution
# This layer is cached unless parent POM changes
COPY pom.xml ../pom.xml
COPY common-lib ../common-lib

# Install parent POM first
RUN cd .. && mvn install -N -DskipTests --no-transfer-progress -B

# Copy service-specific POM and source code
# Copying POM separately allows Docker to cache dependencies
COPY auth-service/pom.xml ./pom.xml

# Build common-lib (required dependency)
RUN cd ../common-lib && mvn clean install -DskipTests --no-transfer-progress -B

# Download dependencies
# This step is cached unless POM files change, speeding up subsequent builds
RUN mvn dependency:go-offline -B

# Copy application source code
COPY auth-service/src ./src

# Build the application
# -DskipTests: Skip tests during Docker build (run separately in CI/CD)
# --no-transfer-progress: Reduce log verbosity
# -B: Batch mode for non-interactive builds
RUN mvn clean package -DskipTests --no-transfer-progress -B

# ------------------------------------------------------------------------------
# Stage 2: Runtime Stage
# ------------------------------------------------------------------------------
# Purpose: Create minimal runtime image for production
# Base Image: JRE only (smaller than JDK) - ARM64/x86_64 compatible
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre

# Metadata labels for container registry
LABEL maintainer="ecommerce-team@example.com"
LABEL service="auth-service"
LABEL version="1.0.0"
LABEL description="Authentication and Authorization Microservice"

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security best practices
# Running as non-root reduces attack surface
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Set working directory
WORKDIR /app

# Copy JAR from build stage
# Only the compiled artifact is copied, not source code or build tools
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose application port
# This is documentation; actual port binding happens at runtime
EXPOSE 8081

# Health check configuration
# Docker will mark container as unhealthy if this fails
# --interval: Time between checks
# --timeout: Maximum time for check to complete
# --start-period: Grace period before starting health checks
# --retries: Number of consecutive failures before marking unhealthy
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/api/v1/auth/health || exit 1

# JVM Configuration
# -XX:+UseContainerSupport: Respect container memory limits
# -XX:MaxRAMPercentage=75.0: Use up to 75% of container memory
# -XX:+ExitOnOutOfMemoryError: Exit on OOM for container restart
# -Djava.security.egd: Faster startup by using non-blocking entropy
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

# Container entry point
# exec form ensures proper signal handling (e.g., SIGTERM)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]




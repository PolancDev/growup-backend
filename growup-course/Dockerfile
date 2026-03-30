# Production stage - slim image for growup-course
FROM eclipse-temurin:17-jre-alpine

# Security: Create non-root user
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser

WORKDIR /app

# Copy the pre-built jar
COPY target/growup-course-1.0.0-SNAPSHOT.jar app.jar

# Create directory for logs
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

# Expose application port
EXPOSE 8082

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"

# Switch to non-root user
USER appuser

# Health check - simple TCP check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD nc -z localhost 8082 || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

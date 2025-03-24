# Use Amazon Corretto JRE 17 (Lightweight Alpine-based)
FROM amazoncorretto:17-alpine AS runtime

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/ishyiga-api.jar app.jar

# Expose the application port
EXPOSE 8086

# Run the application with memory optimizations
ENTRYPOINT ["java", "-jar", "-Xmx256m", "-Xms128m", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "app.jar"]

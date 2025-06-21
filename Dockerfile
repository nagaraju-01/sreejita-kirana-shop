# Use Eclipse Temurin JDK 17 as the base image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar file
COPY target/kirana-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (change if your app uses a different port)
EXPOSE 8080

# Set environment variables for Java (optional, but recommended for containers)
ENV JAVA_OPTS="-XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

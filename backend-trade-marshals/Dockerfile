# Use Maven to build the application (you can use an official Maven image)
FROM maven:3.8.5-openjdk-17-slim AS build
 
# Set the working directory in the container
WORKDIR /app
 
# Copy the pom.xml and the source code into the container
COPY pom.xml .
COPY src ./src
 
# Build the application using Maven
RUN mvn clean package -DskipTests
 
# Use a smaller JDK image to run the application
FROM openjdk:17-jdk-slim
 
# Set the working directory in the container
WORKDIR /app
 
# Copy the built JAR file from the previous stage
COPY --from=build /app/target/BackendTradeMarshals.jar /app/app.jar
 
# Expose the port your application will run on (default 8080)
EXPOSE 8080
 
# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
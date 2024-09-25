FROM amazoncorretto:17.0.7-alpine
WORKDIR /app
COPY ../target/workload-0.0.1-SNAPSHOT.jar /app/workload-0.0.1-SNAPSHOT.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/workload-0.0.1-SNAPSHOT.jar"]

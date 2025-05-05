FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/chat-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 88
CMD ["java", "-jar", "app.jar"]
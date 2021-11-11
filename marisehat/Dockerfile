FROM gcr.io/distroless/java-debian10:11

ARG JAR_FILE=target/*.jar

WORKDIR /app
COPY ${JAR_FILE} app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "./app.jar"]
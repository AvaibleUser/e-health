FROM gcr.io/distroless/java21-debian12

WORKDIR /app

COPY ./build/libs/*.jar /app/e-health.jar

ENTRYPOINT ["java", "-jar", "e-health.jar"]

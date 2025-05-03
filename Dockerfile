FROM eclipse-temurin:21-jdk-alpine as jre-builder

RUN apk update && apk add binutils

WORKDIR /app

RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /jdk-21

FROM alpine:latest

ENV JAVA_HOME=/opt/jdk/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=jre-builder /jdk-21 $JAVA_HOME

ARG OWNER=ceo

WORKDIR /app

RUN addgroup --system $OWNER && \
    adduser --system $OWNER --ingroup $OWNER && \
    chown -R $OWNER /app

COPY --chown=$OWNER:$OWNER ./build/libs/*.jar /app/e-health.jar

USER $OWNER

ENTRYPOINT ["java", "-jar", "e-health.jar"]

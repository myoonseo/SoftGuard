FROM amazoncorretto:17

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "build/libs/*.jar"]
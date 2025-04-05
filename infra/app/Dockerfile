FROM eclipse-temurin:17-jdk

LABEL authors="nathan"

WORKDIR /app

COPY ../../ .

RUN ./mvnw clean package -DskipTests

RUN mkdir -p /app && cp target/*.jar /app/app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]
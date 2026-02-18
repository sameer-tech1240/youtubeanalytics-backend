FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render will give PORT env variable
EXPOSE 10000

ENTRYPOINT ["sh","-c","java -jar /app/app.jar --server.port=${PORT}"]

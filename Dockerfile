FROM maven:3.9.4-eclipse-temurin-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /home/app/target/jet-movie-top-bot-1.0-SNAPSHOT.jar /usr/local/lib/jet-movie-top-bot.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/jet-movie-top-bot.jar"]
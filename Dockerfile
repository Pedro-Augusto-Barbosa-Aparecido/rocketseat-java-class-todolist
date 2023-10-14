FROM ubuntu:latest AS build

# install dependencies
RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

# copy all content to render environment
COPY . .

# install java and maven
RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:17-jdk-slim

# expose port 8080
EXPOSE 8080

# generate application file friendly
COPY --from=build /target/todolist-1.0.0.jar app.jar

# run application
ENTRYPOINT ["java", "-jar", "app.jar"]

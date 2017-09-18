FROM openjdk:8

CMD mkdir /app

COPY flux-flix-service-0.0.1-SNAPSHOT.jar /app

WORKDIR /app
EXPOSE 8080

ENTRYPOINT "java -jar /app/flux-flix-service-0.0.1-SNAPSHOT.jar"
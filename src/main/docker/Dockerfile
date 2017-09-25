FROM openjdk:9-jre

CMD mkdir /myapp

COPY flux-flix-service-0.0.1-SNAPSHOT.jar /myapp/

WORKDIR /myapp

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/myapp/flux-flix-service-0.0.1-SNAPSHOT.jar"]
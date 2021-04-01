FROM openjdk:8u282-jdk
VOLUME /tmp
ADD target/country-code-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

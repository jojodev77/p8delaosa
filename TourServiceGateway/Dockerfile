FROM openjdk:8
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} tourservicegateway.jar
ENTRYPOINT ["java","-jar","/tourservicegateway.jar"]

FROM openjdk:11-jdk

ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} /app.jar

ENTRYPOINT java ${JAVA_OPT} -Djava.security.egd=file:/dev/./urandom -jar /app.jar

FROM openjdk:8-jdk-alpine
MAINTAINER by liujianxin
VOLUME ["/tmp"]
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ "sh", "-c", "java -Dspring.profiles.active=$PROFILES $JAVA_OPTS $APP_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
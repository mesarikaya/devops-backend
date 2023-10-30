FROM amazoncorretto:17
LABEL maintainer="Ergin Sarikaya"
LABEL PROJECT_NAME="devops-rest-api-service"
# Install required utilities
RUN yum update -y
RUN yum install -y shadow-utils

# Add a user and group
RUN groupadd -r user && useradd -r -g user ubuntu
USER ubuntu

ARG SPRING_PROFILES_ACTIVE=default

VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8000
ENTRYPOINT exec java  $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app.jar
FROM amazoncorretto:17
LABEL maintainer="Ergin Sarikaya"
LABEL PROJECT_NAME="devops-rest-api-service"
USER ubuntu

# Fix security issues
RUN yum -y update java-17-amazon-corretto-devel user ubuntu
RUN yum -y update libxml2 user ubuntu
RUN yum -y update zlib user ubuntu
RUN yum -y update expat user ubuntu
RUN yum -y update curl user ubuntu
RUN yum -y update vim-data user ubuntu
RUN yum -y update vim-minimal user ubuntu
RUN yum -y update libblkid user ubuntu
RUN yum -y update libcom_err user ubuntu
RUN yum -y update libmount user ubuntu
RUN yum -y update libuuid user ubuntu
RUN yum -y update ncurses user ubuntu
RUN yum -y update ncurses-base user ubuntu
RUN yum -y update ncurses-libs user ubuntu
RUN yum -y update krb5-libs user ubuntu
RUN yum -y update libpng user ubuntu
RUN yum -y update libtasn1 user ubuntu
RUN yum -y update sqlite user ubuntu
RUN yum -y update openssl-libs user ubuntu
RUN yum -y update ca-certificates user ubuntu

ARG SPRING_PROFILES_ACTIVE=default

VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8000
ENTRYPOINT exec java  $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app.jar
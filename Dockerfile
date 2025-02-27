FROM openjdk:22-jdk
WORKDIR /app
COPY target/*.jar app.jar

ARG BASE_URL_OF_THE_SITE
ARG NOTIFICATION_SERVICE_MAIL_PASSWORD
ARG NOTIFICATION_SERVICE_MAIL_USERNAME
ARG NOTIFICATION_SERVICE_RABBITMQ_PASSWORD
ARG NOTIFICATION_SERVICE_RABBITMQ_USERNAME
ARG NOTIFICATION_SERVICE_REDIS_PASSWORD

ENV BASE_URL_OF_THE_SITE=${BASE_URL_OF_THE_SITE}
ENV NOTIFICATION_SERVICE_MAIL_PASSWORD=${NOTIFICATION_SERVICE_MAIL_PASSWORD}
ENV NOTIFICATION_SERVICE_MAIL_USERNAME=${NOTIFICATION_SERVICE_MAIL_USERNAME}
ENV NOTIFICATION_SERVICE_RABBITMQ_PASSWORD=${NOTIFICATION_SERVICE_RABBITMQ_PASSWORD}
ENV NOTIFICATION_SERVICE_RABBITMQ_USERNAME=${NOTIFICATION_SERVICE_RABBITMQ_USERNAME}
ENV NOTIFICATION_SERVICE_REDIS_PASSWORD=${NOTIFICATION_SERVICE_REDIS_PASSWORD}

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
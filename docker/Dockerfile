# @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>

FROM ubuntu:22.04

# Disable Prompt During Packages Installation
ARG DEBIAN_FRONTEND=noninteractive

ARG SPRING_USER=springuser
ARG USER_HOME=/home/$SPRING_USER

ARG SPRING_BOOT_DEMO_PATH=/core
ARG SPRING_BOOT_DEMO_DOCKER_PATH=${SPRING_BOOT_DEMO_PATH}/docker

RUN apt update && \
    apt upgrade -y && \
    apt install -y \
    sudo \
    nano \
    nmap \
    openjdk-17-jre \
    build-essential

RUN rm -rf /var/lib/apt/lists/* && \
    apt-get autoremove -y && \
    apt clean

RUN useradd -ms /bin/bash $SPRING_USER
USER $SPRING_USER

ENV PATH="$USER_HOME/.local/bin/:$SPRING_BOOT_DEMO_PATH:$SPRING_BOOT_DEMO_DOCKER_PATH:$PATH"

# Set default environment variable for MYSQL_IP_ADDRESS and make it accessible from Jenkins build.job
ENV MYSQL_IP_ADDRESS 0.0.0.0

# Set default environment variable for MYSQL_PORT and make it accessible from Jenkins build.job
ENV MYSQL_PORT 3306

WORKDIR $SPRING_BOOT_DEMO_PATH

# Copy all files from current folder to docker WORKDIR
COPY --chown=$SPRING_USER:$SPRING_USER . .

FROM ubuntu:latest
LABEL Description="LaTeX Render Server"

RUN ln -snf /usr/share/zoneinfo/Etc/UTC /etc/localtime \
    && echo "Etc/UTC" > /etc/timezone \
    && apt-get -qq update \
    && apt-get -qq upgrade -y \
    && apt-get -qq install texlive-latex-base texlive-latex-extra texlive-fonts-recommended lmodern xzdec openjdk-8-jdk -y \
    && rm -rf /var/lib/apt/lists/*
    
ADD maven /root

RUN mkdir -p /var/archive /tmp/renderer \
    && chmod 770 /var/archive /tmp/renderer \
    && chown root:root /var/archive /tmp/renderer

VOLUME /var/archive /tmp/renderer

EXPOSE 8080

ENV JAVA_APP_JAR latex-server.jar
ENV JAVA_APP_DIR /root
ENV JAVA_OPTIONS "-Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=prod"

ENTRYPOINT ["sh", "/root/run-java.sh"]

FROM ubuntu:bionic
LABEL Description="LaTeX Render Server"

RUN ln -snf /usr/share/zoneinfo/Etc/UTC /etc/localtime \
    && echo "Etc/UTC" > /etc/timezone \
    && apt-get update \
    && apt-get upgrade -y \
    && apt-get install texlive-latex-base texlive-latex-extra texlive-fonts-recommended xzdec openjdk-8-jdk -y \
    && rm -rf /var/lib/apt/lists/*
    
ADD maven /root

EXPOSE 8080

RUN mkdir -p /var/archive /tmp/renderer \
    && chmod 770 /var/archive /tmp/renderer \
    && chown root:root /var/archive /tmp/renderer

VOLUME /var/archive /tmp/renderer

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-Dspring.profiles.active=prod", "-jar", "/root/latex-server.jar"]

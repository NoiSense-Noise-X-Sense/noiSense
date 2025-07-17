FROM jenkins/jenkins:lts-jdk17

USER root

#Docker CLI 설치
RUN apt-get update && \
    apt-get install -y \
    docker.io \
    docker-compose && \
    apt-get clean

# Node.js 설치
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

#Docker 명령어가 없다고 나올 수 있으니 /usr/bin에 심볼릭 링크도 설정 (보완책)
RUN ln -s /usr/bin/docker.io /usr/bin/docker || true

#Jenkins 유저도 docker 실행할 수 있도록 그룹 추가
RUN usermod -aG docker jenkins


USER jenkins

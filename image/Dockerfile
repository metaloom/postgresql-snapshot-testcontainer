FROM postgres:13.2

RUN apt-get update  && apt-get install -y rsync psmisc procps coreutils

ADD docker-idle-entrypoint.sh /docker-idle-entrypoint.sh
RUN chmod +x /docker-idle-entrypoint.sh

ENTRYPOINT ["/docker-idle-entrypoint.sh"]

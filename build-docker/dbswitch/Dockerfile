FROM openjdk:8-jre-alpine

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD dbswitch-release.tar.gz  /

EXPOSE      9088

USER        root

WORKDIR /dbswitch-release

RUN chmod u+x /dbswitch-release/bin/startup.sh

CMD  ["/dbswitch-release/bin/startup.sh"]


FROM centos:7

ENV LANG C.UTF-8

RUN yum clean all && \
		yum -y update && \
		yum -y install java-1.8.0-openjdk-devel
ENV JAVA_HOME /usr/lib/jvm/java

ADD http://rpms.adiscon.com/v8-stable/rsyslog.repo /etc/yum.repos.d/rsyslog.repo
RUN		yum -y update && \
		yum -y install rsyslog

WORKDIR /app

ADD gradle gradle
ADD gradlew gradlew

ADD build.gradle build.gradle
RUN ./gradlew eclipse			# force download wrapper and dependencies
ADD src src

RUN mkdir -p build/logs rsyslog/config rsyslog/temp
ADD config rsyslog/config
RUN mv rsyslog/config/main_rsyslog.conf /etc/rsyslog.d/main_rsyslog.conf
ADD inodes.sh inodes.sh

ENV RSYSLOG_DEBUGLOG="/app/rsyslog/debug.log"
ENV RSYSLOG_DEBUG="DebugOnDemand NoStdOut"
CMD /etc/init.d/rsyslog start && ./gradlew run

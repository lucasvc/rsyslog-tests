# Log4j2 and rsyslog with rolling files

[E-mail thread at rsyslog mailing list](http://lists.adiscon.net/pipermail/rsyslog/2017-June/044407.html).

## How to run

First build the docker image, `cd` to this folder and:

```
docker build -t rsyslog-log4j2-rollover .
```

Later run the container with:

```
docker run rsyslog-log4j2-rollover
```

Will check automatically if the number of lines are the same.

## (!) WARNING debug
At `Dockerfile` there is the environment variable `RSYSLOG_DEBUG` with value `DebugOnDemand`, because when switching to `Debug` the container won't end, because rsyslog doesn't write all the logs.

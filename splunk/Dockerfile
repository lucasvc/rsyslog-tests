FROM splunk/splunk:6.6.0

ENV SPLUNK_START_ARGS="--accept-license --answer-yes" SPLUNK_ENABLE_LISTEN="9997" \
	SPLUNK_ADD="tcp 1514" SPLUNK_USER="root"

RUN find $SPLUNK_HOME -print | xargs --max-args=1 --max-procs=4 chown root:splunk
COPY config /opt/splunk/etc

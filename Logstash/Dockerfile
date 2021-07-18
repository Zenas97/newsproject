FROM logstash:7.13.2

RUN rm -f /usr/share/logstash/pipeline/logstash.conf
COPY mysql-connector-java-8.0.20.jar /usr/share/logstash/logstash-core/lib/jars
COPY protobuf-java-3.6.1.jar /usr/share/logstash/logstash-core/lib/jars
COPY mypipeline.conf /usr/share/logstash/pipeline
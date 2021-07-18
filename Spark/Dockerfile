FROM bitnami/spark

USER root
WORKDIR /opt/bitnami/spark/bin
CMD ./spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.2,org.elasticsearch:elasticsearch-spark-30_2.12:7.13.3 --class mypack.myDemo /opt/bitnami/spark/program/myDemo.jar



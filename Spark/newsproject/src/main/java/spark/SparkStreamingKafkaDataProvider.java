package spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class SparkStreamingKafkaDataProvider implements DataProvider {

	SparkSession sparkSession;
	StructType schema;
	public SparkStreamingKafkaDataProvider(SparkSession sparkSession) {
		this.sparkSession = sparkSession;
		this.schema = DataTypes.createStructType(new StructField[] {
				DataTypes.createStructField("id", DataTypes.LongType, false),
				DataTypes.createStructField("titolo", DataTypes.StringType, false),
				DataTypes.createStructField("autore", DataTypes.StringType, false),
				DataTypes.createStructField("testo", DataTypes.StringType, false),
				DataTypes.createStructField("@timestamp",DataTypes.StringType,false)
			});	
	}
	
	@Override
	public Dataset<Row> getData() {
		Dataset<Row> df = sparkSession.readStream().format("kafka")
		.option("kafka.bootstrap.servers", "kafkaHost:9092")
		.option("subscribe","newsTopic")
		.option("startingOffsets","latest")
		.option("maxOffsetsPerTrigger",5)
		.load();
		return df;
	}


	@Override
	public StructType getDataSchema() {	
		return schema;
	}

}

package spark;

import java.util.concurrent.TimeoutException;

import org.apache.spark.SparkConf;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import static org.apache.spark.sql.functions.udf;
import org.apache.spark.ml.linalg.Vector;

import exception.ModelNotFoundException;

public class FakeNewsStreaming {
	
	DataProvider data;
	PipelineModel model;
	SparkSession spark;
	
	
	public FakeNewsStreaming() throws StreamingQueryException, TimeoutException, ModelNotFoundException {
		generateSparkSession();
		injectDataProvider();
		injectPredictionModel();
	}

	
	public void run() throws StreamingQueryException, TimeoutException 
	{
		Dataset<Row> df = data.getData();
		

		//Manipolazione dati
		df = extractKafkaValueField(df);
		df = transformValueFieldColumnToJsonColumn(df);
		df = renameColumns(df);
		df = makePrediction(df);
		df = getFinalOutputFromPrediction(df);
	
		//Scrittura dati
		streamToConsole(df);
		streamToES(df);
	}

	
	/*
	 * Manipolazione dataframe
	 */
	private Dataset<Row> getFinalOutputFromPrediction(Dataset<Row> df) {
		//Estrae la label testuale dal risultato della predizione che è numerico
		df = df.withColumn("risultato",functions.when(df.col("prediction").equalTo(1.0),"FAKE").otherwise("TRUE"));
		//Estrae la probabilità associata a questa predizione dal Vector delle probabilità
		df = df.withColumn("Probabilita",functions.when(df.col("risultato").equalTo("FAKE"),functions.callUDF("fakeProb", df.col("probability"))).otherwise(functions.callUDF("trueProb", df.col("probability"))));
		return df.selectExpr("id","autore","title as titolo","text as testo","data","risultato","round(Probabilita,3) as Probabilita");
	}

	private Dataset<Row> extractKafkaValueField(Dataset<Row> df) {
		return df.selectExpr("CAST(value AS STRING) as dati");
	}
	
	
	private Dataset<Row> renameColumns (Dataset<Row> df) {
		return df.select(df.col("dati2.id"),df.col("dati2.titolo").alias("title"),df.col("dati2.autore").alias("autore"),df.col("dati2.testo").alias("text"),df.col("dati2.@timestamp").alias("data"));
	}
	

	private Dataset<Row> transformValueFieldColumnToJsonColumn (Dataset<Row> df) {
		return df.select(functions.from_json(df.col("dati"),data.getDataSchema()).alias("dati2"));
	}

	private Dataset<Row> makePrediction(Dataset<Row> df) {
		return model.transform(df);
	}
	
	/*
	 * 
	 */
	
	
	/*
	 * Costruzione dell'oggetto
	 */
	
	private void injectDataProvider() {
		data = new SparkStreamingKafkaDataProvider(spark);
	}

	private void generateSparkSession() {
		UserDefinedFunction getTrueProbability =  udf((Vector v)->v.apply(0),DataTypes.DoubleType);
		UserDefinedFunction getFakeProbability =  udf((Vector v)->v.apply(1),DataTypes.DoubleType);
		SparkConf config = getSparkConfiguration();
		spark = SparkSession.builder().config(config).getOrCreate();
		//Log spark level
		spark.sparkContext().setLogLevel("WARN");
		System.out.println("Some spark session activated");
		spark.udf().register("trueProb",getTrueProbability);
		spark.udf().register("fakeProb",getFakeProbability);
	}
	
	private SparkConf getSparkConfiguration() {
		return new SparkConf().setMaster("local").setAppName("fakeNews")
				.set("es.index.auto.create", "true")
				.set("es.nodes", "elasticHost:9200");
	}
	
	private void injectPredictionModel() throws ModelNotFoundException {
		model = PipelineModel.load("/opt/bitnami/spark/model");
		if(model == null) throw new ModelNotFoundException("couldn't load model");

	}
	
	/*
	 * 
	 * 
	 */
	
	/*
	 * Streaming Output
	 */
	private void streamToES(Dataset<Row> df) {
		df.writeStream()
		.format("es")
		.option("checkpointLocation", "/tmp/checkpointLocation")
		.start("news");
	}
	
	private void streamToConsole(Dataset<Row> df) throws TimeoutException {
		df.writeStream()
		.format("console")
		.start();
	}
	/*
	 * 
	 * 
	 */

}

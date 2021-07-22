package spark;

import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

public class SparkTrainingJsonDataProvider implements DataProvider{

	SparkSession sparkSession;
	File fakeNews = new File("/opt/bitnami/spark/program/fake_or_real_news.json");
	
	public SparkTrainingJsonDataProvider(SparkSession sparkSession) {
		this.sparkSession = sparkSession;
	}
	
	@Override
	public Dataset<Row> getData() {
		Dataset<Row> fakeNewsDataset = null;
		try {
				fakeNewsDataset = sparkSession.read()
					.format("json")
					.option("multiline","true")
					.option("mode","DROPMALFORMED")
					.option("inferSchema",true)
					.load(fakeNews.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fakeNewsDataset;
	}

	@Override
	public StructType getDataSchema() {
		return null;
	}

}

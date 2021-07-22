package spark;

import java.io.File;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

public interface DataProvider 
{
	Dataset<Row> getData();
	StructType getDataSchema();
}

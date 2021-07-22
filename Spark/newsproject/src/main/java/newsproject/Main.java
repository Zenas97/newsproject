package newsproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import exception.ModelNotFoundException;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

import scala.collection.Seq;
import spark.FakeNewsTraining;
import spark.FakeNewsStreaming;
public class Main {

	public static void main(String[] args) throws TimeoutException, StreamingQueryException, InterruptedException, IOException, ModelNotFoundException {
		
		FakeNewsStreaming demo = new FakeNewsStreaming();
		demo.run();
		/*
		FakeNewsTraining demo = new FakeNewsTraining();
		demo.run();
		*/
		while(true) {
			
		}
		
	}

}

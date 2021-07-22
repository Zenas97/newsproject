package spark;

import java.io.File;
import java.io.IOException;

import org.apache.avro.data.Json;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.feature.CountVectorizer;
import org.apache.spark.ml.feature.NGram;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.StopWordsRemover;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.feature.VectorIndexer;
import org.apache.spark.ml.feature.Word2Vec;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.ml.tuning.TrainValidationSplit;
import org.apache.spark.ml.tuning.TrainValidationSplitModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.storage.StorageLevel;

public class FakeNewsTraining 
{
	DataProvider data;
	SparkSession spark;
	
	Dataset<Row> trainingSet;
	Dataset<Row> testSet;
	
	public FakeNewsTraining() throws IOException {
		generateSparkSession();
		injectDataProvider();
		
	}
	
	public void run() throws IOException {
		
		Dataset<Row> fakeNewsDataset = data.getData();
		
		
		//Label Indexing Phase
		fakeNewsDataset = labelIndexing(fakeNewsDataset);
		fakeNewsDataset.show(20);
		
		//Dataset Split Phase
		generateTrainingAndTestDataset(fakeNewsDataset);
	
		
		//Text Preprocessing Phase	
		Tokenizer tokenizeText = getTokenizer("text", "tokenizedText");
		Tokenizer tokenizeTitle = getTokenizer("title", "tokenizedTitle");	
		
		StopWordsRemover removeTokenizedTextStopWords = getEnglishStopWordRemover("tokenizedText", "tokenizedTextNoStopWords");
		StopWordsRemover removeTokenizedTitleStopWords = getEnglishStopWordRemover("tokenizedText", "tokenizedTitleNoStopWords");
	
		NGram textTrigrams = getTrigram("tokenizedTextNoStopWords", "textTrigrams");
		NGram titleTrigrams = getTrigram("tokenizedTitleNoStopWords", "titleTrigrams");
		
		CountVectorizer textVectorizer = getVectorizer("textTrigrams", "textVector");
		CountVectorizer titleVectorizer = getVectorizer("titleTrigrams", "titleVector");
		
		VectorAssembler vectorAssembler = getVectorAssembler(new String[]{"textVector","titleVector"},"features");
		
		LogisticRegression logisticRegression = getLogisticRegression("labelIndex", "features");
		
		
		//Training Phase
		PipelineStage[] trainingPipelineStages = new PipelineStage[]{tokenizeText,tokenizeTitle,removeTokenizedTextStopWords,removeTokenizedTitleStopWords,
				textTrigrams,titleTrigrams,textVectorizer,titleVectorizer,vectorAssembler,logisticRegression};
		
		PipelineModel fittedModel = trainModel(trainingSet,trainingPipelineStages);
		
		saveFittedModel(fittedModel);
		
		
		//Evaluation Phase
		testAndEvaluateModel(testSet, fittedModel);
		
	}
	
	/*
	 * Indexing and splitting
	 */
	private Dataset<Row> labelIndexing(Dataset<Row> fakeNewsDataset) {
		fakeNewsDataset = fakeNewsDataset.withColumn("labelIndex",functions.when(fakeNewsDataset.col("label").equalTo("FAKE"),1.0).otherwise(0.0));
		return fakeNewsDataset;
	}

	private void generateTrainingAndTestDataset(Dataset<Row> fakeNewsDataset) {
		Dataset<Row>[] sets = fakeNewsDataset.randomSplit(new double[] {0.7,0.3});
		trainingSet = sets[0];
		testSet = sets[1];
	}
	/*
	 * 
	 */
	
	/*
	 * Preprocessing
	 */
	private Tokenizer getTokenizer(String inputCol,String outputCol) {
		return new Tokenizer().setInputCol(inputCol).setOutputCol(outputCol);
	}
	
	private StopWordsRemover getEnglishStopWordRemover(String inputCol,String outputCol) {
		String[] wordsToRemove = StopWordsRemover.loadDefaultStopWords("english");
		return new StopWordsRemover().setInputCol(inputCol).setOutputCol(outputCol).setStopWords(wordsToRemove);
	}
	
	private NGram getTrigram(String inputCol,String outputCol) {
		return new NGram().setInputCol(inputCol).setOutputCol(outputCol).setN(3);
	}
	
	private CountVectorizer getVectorizer(String inputCol,String outputCol) {
		return new CountVectorizer().setInputCol(inputCol).setOutputCol(outputCol);
	}
	
	private VectorAssembler getVectorAssembler(String[] cols,String outputCol) {
		return new VectorAssembler().setInputCols(cols).setOutputCol(outputCol);
	}
	
	private LogisticRegression getLogisticRegression(String labelCol,String featuresCol) {
		return new LogisticRegression().setLabelCol(labelCol).setFeaturesCol(featuresCol);
	}
	/*
	 * 
	 */
	
	/*
	 * Training Phase
	 */
	private PipelineModel trainModel(Dataset<Row> trainingSet,PipelineStage[] stages) throws IOException {
		return new Pipeline().setStages(stages).fit(trainingSet);
	}
	
	private void saveFittedModel(PipelineModel fittedModel) throws IOException {
		fittedModel.write().overwrite().save("/opt/bitnami/spark/model");
	}
	/*
	 * 
	 */

	/*
	 * Evaluation Phase
	 */
	private BinaryClassificationEvaluator createEvaluator() {
		BinaryClassificationEvaluator evaluator = new BinaryClassificationEvaluator();
		evaluator.setMetricName("areaUnderROC")
		.setRawPredictionCol("prediction")
		.setLabelCol("labelIndex");
		return evaluator;
	}
	
	private void testAndEvaluateModel(Dataset<Row> testSet, PipelineModel fittedModel) {
		BinaryClassificationEvaluator evaluator = createEvaluator();
		System.out.println("precision : " + evaluator.evaluate(fittedModel.transform(testSet)));
	}
	/*
	 * 
	 */

	
	/*
	 * Costruzione dell'oggetto
	 */
	private void generateSparkSession() {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("fakeNews").set("spark.local.dir","tmp").set("spark.driver.memory", "4g").set("spark.executor.memory", "4g")
				.set("spark.memory.fraction","1").set("spark.memory.storageFraction","0.95")
				.set("spark.memory.offHeap.enabled", "true").set("spark.memory.offHeap.size", "2g");
		spark = SparkSession.builder().config(conf).getOrCreate();
		
		//Setting log level
		spark.sparkContext().setLogLevel("WARN");
	}
	
	private void injectDataProvider() {
		data = new SparkTrainingJsonDataProvider(spark);
		
	}
	/*
	 * 
	 * 
	 */
}

package it.polimi.middleware.spark.enrichment;

import it.polimi.middleware.spark.utils.LogUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeoutException;

public class NoiseEnrichment {
        public static void main(String[] args) throws TimeoutException, StreamingQueryException {
                LogUtils.setLogLevel();

                StructType readingSchema = DataTypes.createStructType(new StructField[] {
                                DataTypes.createStructField("X", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Y", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Average Exceeded", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Noise Level (dB)", DataTypes.StringType, true),
                });

                final String master = args.length > 0 ? args[0] : "local[4]";

                final SparkSession spark = SparkSession
                                .builder()
                                .master(master)
                                .appName("NoiseEnrichment")
                                .getOrCreate();

                Dataset<Row> poiDataset = spark.
                                .read()
                                .option("header", "true")
                                .csv("../files/poi_italy.csv");

                poliDataset.createOrReplaceTempView("Poi");

                Dataset<Row> noiseStream = spark
                                .readStream()
                                .format("kafka")
                                .option("kafka.bootstrap.servers", "localhost:9092")
                                .option("subscribe", "clean_noise_readings")
                                .load();

                noiseStream = noiseStream.withColumn("strVal", df.col("value").cast("String"));

                noiseStream = noiseStream.withColumn("reading", org.apache.spark.sql.functions.from_json(df.col("strVal").cast("String"),
                                                 readingSchema));

                noiseStream.createOrReplaceTempView("CleanNoise");

                // Query
                StreamingQuery query = spark
                                .sql("SELECT N.timestamp, N.reading.myID, N.reading.X, N.reading.Y, N.reading.`Average Exceeded`, N.reading.`Noise Level (dB)`, P.name, P.country FROM CleanNoise AS N,Poi AS P WHERE (N.x - P.Latitude) +   >= ")
                                .writeStream()
                                .outputMode("update")
                                .format("console")
                                .start();
                                
                query.awaitTermination();

                spark.close();

        }
}

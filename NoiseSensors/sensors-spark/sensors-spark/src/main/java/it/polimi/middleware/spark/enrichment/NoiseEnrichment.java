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
import static org.apache.spark.sql.functions.*;

import java.util.concurrent.TimeoutException;

public class NoiseEnrichment {
        public static void main(String[] args) throws TimeoutException, StreamingQueryException {
                LogUtils.setLogLevel();

                StructType readingSchema = DataTypes.createStructType(new StructField[] {
                                DataTypes.createStructField("Seq #", DataTypes.IntegerType, true),
                                DataTypes.createStructField("X", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Y", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Average Exceeded", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Noise Level (dB)", DataTypes.StringType, true),
                });

                final String master = args.length > 0 ? args[0] : "local[2]";

                final SparkSession spark = SparkSession
                                .builder()
                                .master(master)
                                .appName("NoiseEnrichment")
                                .getOrCreate();

                Dataset<Row> poiDataset = spark
                                .read()
                                .option("header", "true")
                                .csv("/media/sf_MTDS-Projects/NoiseSensors/sensors-spark/sensors-spark/files/poi_italy copy.csv");

                poiDataset.createOrReplaceTempView("Poi");

                // poiDataset.show();

                Dataset<Row> noiseStream = spark
                                .readStream()
                                .format("kafka")
                                .option("kafka.bootstrap.servers", "localhost:9092")
                                .option("subscribe", "clean_noise_readings")
                                .load();

                noiseStream = noiseStream.withColumn("strVal", noiseStream.col("value").cast("String"));

                noiseStream = noiseStream.withColumn("reading",
                                org.apache.spark.sql.functions.from_json(noiseStream.col("strVal").cast("String"),
                                                readingSchema));

                noiseStream.createOrReplaceTempView("CleanNoise");

                // Query
                Dataset<Row> noisePOI = spark
                                .sql("SELECT N.reading.`Seq #`, N.timestamp, N.reading.X, N.reading.Y, N.reading.`Average Exceeded`, N.reading.`Noise Level (dB)`, P.name, P.region,  (N.reading.X +  P.Latitude + N.reading.Y + P.Longitude) AS Dist FROM CleanNoise AS N CROSS JOIN Poi AS P");                

                noisePOI
                        .withWatermark("timestamp", "10 seconds")
                        .createOrReplaceTempView("NoisePOI");

                Dataset<Row> minDist = spark
                                .sql("SELECT `Seq #`, MIN(Dist) FROM NoisePOI GROUP BY `Seq #`, timestamp");    
                                
                minDist.createOrReplaceTempView("MinDist");

                StreamingQuery query = spark
                                .sql("SELECT * FROM MinDist AS MD JOIN NoisePOI AS NP ON MD.`Seq #` =  NP.`Seq #` AND MD.`min(Dist)` = NP.Dist ")
                                .writeStream()
                                .outputMode("Append")
                                .format("console")
                                .start();

                query.awaitTermination();

                spark.close();

        }
}

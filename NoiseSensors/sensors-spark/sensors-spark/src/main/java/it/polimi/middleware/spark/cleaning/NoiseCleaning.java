package it.polimi.middleware.spark.cleaning;

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

public class NoiseCleaning {
        public static void main(String[] args) throws TimeoutException, StreamingQueryException {
                LogUtils.setLogLevel();

                StructType readingSchema = DataTypes.createStructType(new StructField[] {
                                DataTypes.createStructField("sensorID", DataTypes.IntegerType, true),
                                DataTypes.createStructField("lat", DataTypes.IntegerType, true),
                                DataTypes.createStructField("lon", DataTypes.IntegerType, true),
                                DataTypes.createStructField("timestamp", DataTypes.IntegerType, true),
                                DataTypes.createStructField("averageExceeded", DataTypes.IntegerType, true),
                                DataTypes.createStructField("noiseVa", DataTypes.StringType, true),
                });

                final String master = args.length > 0 ? args[0] : "local[2]";

                final SparkSession spark = SparkSession
                                .builder()
                                .master(master)
                                .appName("NoiseCleaning")
                                .getOrCreate();

                Dataset<Row> df = spark
                                .readStream()
                                .format("kafka")
                                .option("kafka.bootstrap.servers", "localhost:9092")
                                .option("subscribe", "raw_noise_readings")
                                .load();

                df = df.withColumn("strVal", df.col("value").cast("String"));

                df = df.withColumn("reading", from_json(
                                df.col("strVal"),
                                readingSchema));

                df.createOrReplaceTempView("RawNoise");

                // Query
                StreamingQuery query = spark
                                .sql("SELECT reading.timestamp, reading.sensorID,reading.lat, reading.lon, reading.averageExceeded, reading.noiseVa FROM RawNoise WHERE ((reading.averageExceeded = 0 and reading.noiseVa > 0) or (reading.averageExceeded = 1 and reading.noiseVa NOT REGEXP '-'))")
                                .select(to_json(
                                                struct(
                                                        "timestamp",
                                                        "sensorID",
                                                        "lat",
                                                        "lon",
                                                        "averageExceeded",
                                                        "noiseVa"))
                                                .alias("value"))
                                .writeStream()
                                .format("kafka")
                                .option("checkpointLocation", "/tmp/checkpoints")
                                .option("kafka.bootstrap.servers", "localhost:9092")
                                .option("topic", "clean_noise_readings")
                                .start();

                query.awaitTermination();

                spark.close();

        }
}

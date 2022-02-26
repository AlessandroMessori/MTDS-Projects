package it.polimi.middleware.spark.cleaning;

import it.polimi.middleware.spark.utils.LogUtils;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeoutException;

public class NoiseCleaning {
        public static void main(String[] args) throws TimeoutException, StreamingQueryException {
                LogUtils.setLogLevel();

                // {"d":{"myName":"native","Seq #":173,"Uptime (sec)":1771,"Temp (C)":25,"Def
                // Route":"fe80::212:7401:1:101"}}

                StructType payloadSchema = DataTypes.createStructType(new StructField[] {
                                DataTypes.createStructField("d", DataTypes.StringType, true),
                });

                StructType readingSchema = DataTypes.createStructType(new StructField[] {
                                DataTypes.createStructField("myName", DataTypes.StringType, true),
                                DataTypes.createStructField("Seq #", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Uptime (sec)", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Temp (C)", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Def Route", DataTypes.StringType, true)
                });

                final String master = args.length > 0 ? args[0] : "local[4]";

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

                df = df.withColumn("payload", org.apache.spark.sql.functions.from_json(df.col("strVal"),
                                                payloadSchema));

                df = df.withColumn("reading", org.apache.spark.sql.functions.from_json(df.col("payload").cast("String").substr(2, 100),
                                                 readingSchema));

                // Query
                StreamingQuery query = df
                                .select(df.col("reading.Seq #"), df.col("reading.Temp (C)"))
                                .writeStream()
                                .outputMode("update")
                                .format("console")
                                .start();

                query.awaitTermination();

                spark.close();

        }
}

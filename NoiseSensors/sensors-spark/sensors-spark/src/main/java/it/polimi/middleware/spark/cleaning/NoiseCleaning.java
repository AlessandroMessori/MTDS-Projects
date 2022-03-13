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

                // {"d":{"myName":"native","Seq #":173,"Uptime (sec)":1771,"Temp (C)":25,"Def
                // Route":"fe80::212:7401:1:101"}}

                // {"d":{"myID":12,"X":537,"Y":2203,"Seq #":4,"Uptime (sec)":320,"Average
                // Exceeded":1,"Noise Level (dB)":"[ 81 48 84 5 ]","Def
                // Route":"fe80::201:1:1:1"}}

                // {"d":{"myID":12,"X":537,"Y":2203,"Seq #":4,"Uptime (sec)":320,"Average
                // Exceeded":0,"Noise Level (dB)":"55","Def Route":"fe80::201:1:1:1"}}

                StructType payloadSchema = DataTypes.createStructType(new StructField[] {
                                DataTypes.createStructField("d", DataTypes.StringType, true),
                });

                StructType readingSchema = DataTypes.createStructType(new StructField[] {
                                DataTypes.createStructField("myID", DataTypes.IntegerType, true),
                                DataTypes.createStructField("X", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Y", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Seq #", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Uptime (sec)", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Average Exceeded", DataTypes.IntegerType, true),
                                DataTypes.createStructField("Noise Level (dB)", DataTypes.StringType, true),
                                DataTypes.createStructField("Def Route", DataTypes.StringType, true)
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

                df = df.withColumn("payload", from_json(df.col("strVal"), payloadSchema));

                df = df.withColumn("reading", from_json(
                                df.col("payload").cast("String").substr(2, 1000),
                                readingSchema));

                df.createOrReplaceTempView("RawNoise");

                // Query
                StreamingQuery query = spark
                                .sql("SELECT timestamp, reading.myID, reading.`Seq #`, reading.X, reading.Y, reading.`Average Exceeded`, reading.`Noise Level (dB)` FROM RawNoise WHERE ((reading.`Average Exceeded` = 0 and reading.`Noise Level (dB)` > 0) or (reading.`Average Exceeded` = 1 and reading.`Noise Level (dB)` NOT REGEXP '-'))")
                                .select(to_json(
                                                struct(
                                                        "timestamp",
                                                        "myID",
                                                        "X",
                                                        "Y",
                                                        "`Seq #`",
                                                        "`Average Exceeded`",
                                                        "`Noise Level (dB)`"))
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

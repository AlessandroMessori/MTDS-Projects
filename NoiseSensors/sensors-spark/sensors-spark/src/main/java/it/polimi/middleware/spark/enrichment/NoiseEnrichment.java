package it.polimi.middleware.spark.enrichment;

import it.polimi.middleware.spark.utils.LogUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF4;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import static org.apache.spark.sql.functions.*;

import java.util.concurrent.TimeoutException;

public class NoiseEnrichment {

        private static double deg2rad(double deg) {
                return deg * (Math.PI / 180);
        }

        private static UDF4<Double,Double,Double,Double,Double> distanceFromGEO()
        {
          return ( lat1, lon1, lat2, lon2) -> {
                // Code taken by Stack Overflow

                int R = 6371; // Radius of the earth in km
                double dLat = deg2rad(lat2-lat1);  // deg2rad below
                double dLon = deg2rad(lon2-lon1); 

                double a = 
                  Math.sin(dLat/2) * Math.sin(dLat/2) +
                  Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
                  Math.sin(dLon/2) * Math.sin(dLon/2)
                  ; 

                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
                double d = R * c; // Distance in km
                return d;
          };

        }

        public static void main(String[] args) throws TimeoutException, StreamingQueryException {
                LogUtils.setLogLevel();

                StructType readingSchema = DataTypes.createStructType(new StructField[] {
                        DataTypes.createStructField("sensorID", DataTypes.IntegerType, true),
                        DataTypes.createStructField("lat", DataTypes.DoubleType, true),
                        DataTypes.createStructField("lon", DataTypes.DoubleType, true),
                        DataTypes.createStructField("timestamp", DataTypes.IntegerType, true),
                        DataTypes.createStructField("averageExceeded", DataTypes.IntegerType, true),
                        DataTypes.createStructField("noiseVal", DataTypes.StringType, true),
        });

                final String master = args.length > 0 ? args[0] : "local[2]";

                final SparkSession spark = SparkSession
                                .builder()
                                .master(master)
                                .appName("NoiseEnrichment")
                                .getOrCreate();


                                
                spark.udf().register("GEO_DISTANCE", distanceFromGEO(), DataTypes.DoubleType);

                Dataset<Row> poiDataset = spark
                                .read()
                                .option("header", "true")
                                .csv("/home/alle/Repos/MTDS-Projects/NoiseSensors/sensors-spark/sensors-spark/files/poi_italy.csv");

                poiDataset.createOrReplaceTempView("Poi");

                Dataset<Row> noiseStream = spark
                                .readStream()
                                .format("kafka")
                                .option("failOnDataLoss", "false")
                                .option("kafka.bootstrap.servers", "localhost:9092")
                                .option("subscribe", "raw_noise_readings")
                                .load();

                noiseStream = noiseStream.withColumn("strVal", noiseStream.col("value").cast("String"));

                noiseStream = noiseStream.withColumn("reading",
                                from_json(noiseStream.col("strVal").cast("String"), readingSchema));

                noiseStream.createOrReplaceTempView("CleanNoise");

                // Query
                Dataset<Row> noisePOI = spark
                                .sql("SELECT N.reading.sensorID, N.timestamp, N.reading.lat, N.reading.lon, N.reading.averageExceeded, N.reading.noiseVal, P.name, P.region,  GEO_DISTANCE(N.reading.lat, DOUBLE(P.Latitude), N.reading.lon, DOUBLE(P.Longitude)) AS Dist FROM CleanNoise AS N CROSS JOIN Poi AS P");

                noisePOI
                                .withWatermark("timestamp", "10 seconds")
                                .createOrReplaceTempView("NoisePOI");

                Dataset<Row> minDist = spark
                                .sql("SELECT sensorID, MIN(Dist) FROM NoisePOI GROUP BY sensorID, timestamp");

                minDist.createOrReplaceTempView("MinDist");

                StreamingQuery query = spark
                                .sql("SELECT * FROM MinDist AS MD JOIN NoisePOI AS NP ON MD.sensorID =  NP.sensorID AND MD.`min(Dist)` = NP.Dist ")
                                .writeStream()
                                .format("csv")
                                //.trigger("10 seconds")
                                .option("checkpointLocation", "/home/")
                                .option("path", "/home/checkpointsSpark")
                                .outputMode("append")
                                .start();

                /*StreamingQuery query =spark
                .sql("SELECT N.reading.sensorID, N.timestamp, N.reading.lat, N.reading.lon, N.reading.averageExceeded, N.reading.noiseVal, P.name, P.region, P.latitude, P.longitude  FROM CleanNoise AS N CROSS JOIN Poi AS P")
                .writeStream()
                .format("console")
                .option("checkpointLocation", "/home/checks")
                .outputMode("append")
                .start();*/


                query.awaitTermination();

                spark.close();
        }
}

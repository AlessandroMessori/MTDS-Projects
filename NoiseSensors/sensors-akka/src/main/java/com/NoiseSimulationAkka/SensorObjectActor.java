package com.NoiseSimulationAkka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.LinkedList;
import java.util.Queue;

public class SensorObjectActor extends AbstractActor {
    private static final String simulationTopic = "raw_noise_readings";

    private static final int numMessages = 100;
    private static final int waitBetweenMsgs = 100;
    private static final boolean waitAck = true;

    private static final String serverAddr = "localhost:9092";
    //a higher number 1-1000 random order number
    //also the timestamp to be diff
    private final int ID;
    private double posX;
    private double posY;

    private double maxNoiseLevelPeople;
    private double minNoiseLevelPeople;

    private double maxNoiseLevelVehicles;
    private double minNoiseLevelVehicles;

    private Queue<NoiseReadingMessage> readings;
    private boolean threshold;
    private final int queueSize;


    public SensorObjectActor(double posX, double posY, int queueSize,
                             double maxNoiseLevelPeople, double minNoiseLevelPeople,
                             double maxNoiseLevelVehicles, double minNoiseLevelVehicles,
                             int ID) {
        this.posX = posX;
        this.posY = posY;
        this.readings = new LinkedList<NoiseReadingMessage>();
        this.queueSize = queueSize;
        this.threshold = false;
        this.maxNoiseLevelPeople = maxNoiseLevelPeople;
        this.minNoiseLevelPeople = minNoiseLevelPeople;
        this.maxNoiseLevelVehicles = maxNoiseLevelVehicles;
        this.minNoiseLevelVehicles = minNoiseLevelVehicles;
        this.ID = ID;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NoiseReadingMessage.class, this::onReading)
                .build();
    }

    void onReading(NoiseReadingMessage reading) {
        final Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        final KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        this.readings.add(reading);

        if (this.readings.size() > this.queueSize) {
            this.readings.remove();
        }

        int objType = reading.getObjType();
        double movingAvg = this.getMovingAgv();

        if(objType == 1) {
            this.threshold = movingAvg > maxNoiseLevelVehicles;
        } else if (objType == 0){
            this.threshold = movingAvg > maxNoiseLevelPeople;
        }

        //also modify for the threshold

        String allVal = "[";
        for(NoiseReadingMessage read: readings) {
            allVal += read.toStringVal() + " ";
        }
        allVal += "]";

        final String topic = simulationTopic;
        final String key = "Key" + reading.getTimestamp();
        final String jsonString = new JSONObject()
                .put("sensorID", ID)
                .put("lat", posX)
                .put("lon", posY)
                .put("noiseVal", this.threshold ? allVal : this.getMovingAgv())
                .put("timestamp", reading.getTimestamp())
                .put("averageExceeded", this.threshold ? 1 : 0)
                .toString();
        System.out.println(jsonString);

        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, jsonString);
        final Future<RecordMetadata> future = producer.send(record);

        if (waitAck) {
            try {
                RecordMetadata ack = future.get();
                System.out.println("Ack for topic " + ack.topic() + ", partition " + ack.partition() + ", offset " + ack.offset());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        }

        try {
            Thread.sleep(waitBetweenMsgs);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        //System.out.println(this.readings);
        //System.out.println("Moving Average --> " + this.getMovingAgv());
        //System.out.println("Threshold " + (this.threshold ? "" : "Not ")  + "Exceeded \n");

        producer.close();
        this.moveSensor();

    }

    private void moveSensor() {
        this.posX += 0.005;
        this.posY -= 0.005;
    }

    double getMovingAgv() {

        int size = this.readings.size();

        return this.readings
                .stream()
                .mapToDouble(NoiseReadingMessage::getVal)
                .reduce(0, (subtotal, element) -> subtotal + element / size);
    }


    static Props props() {
        return Props.create(SensorObjectActor.class);
    }
}

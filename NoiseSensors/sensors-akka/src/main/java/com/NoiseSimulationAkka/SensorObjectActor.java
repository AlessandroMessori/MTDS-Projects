package com.NoiseSimulationAkka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

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
    private static final int waitBetweenMsgs = 500;
    private static final boolean waitAck = true;

    private static final String serverAddr = "localhost:9092";

    private int posX;
    private int posY;

    private double maxNoiseLevelPeople;
    private double minNoiseLevelPeople;

    private double maxNoiseLevelVehicles;
    private double minNoiseLevelVehicles;

    private Queue<NoiseReadingMessage> readings;
    private boolean threshold;
    private final int queueSize;


    public SensorObjectActor(int posX, int posY, int queueSize,
                             double maxNoiseLevelPeople, double minNoiseLevelPeople,
                             double maxNoiseLevelVehicles, double minNoiseLevelVehicles) {
        this.posX = posX;
        this.posY = posY;
        this.readings = new LinkedList<NoiseReadingMessage>();
        this.queueSize = queueSize;
        this.threshold = false;
        this.maxNoiseLevelPeople = maxNoiseLevelPeople;
        this.minNoiseLevelPeople = minNoiseLevelPeople;
        this.maxNoiseLevelVehicles = maxNoiseLevelVehicles;
        this.minNoiseLevelVehicles = minNoiseLevelVehicles;
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

        final String topic = simulationTopic;
        final String key = "Key" + reading.getTimestamp();
        //the value should be the temp posx posy
        final String value = "Value" + "," + this.getMovingAgv() + "," + reading.getPosX() + "," + reading.getPosY();

        System.out.println(
                "Topic: " + topic +
                        "\tKey: " + key +
                        "\tValue: " + value
        );

        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
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

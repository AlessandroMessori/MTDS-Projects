package com.NoiseSimulationAkka;


import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.LinkedList;
import java.util.Queue;

public class SensorObjectActor extends AbstractActor {
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

        System.out.println(this.readings);
        System.out.println("Moving Average --> " + this.getMovingAgv());
        System.out.println("Threshold " + (this.threshold ? "" : "Not ")  + "Exceeded \n");

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

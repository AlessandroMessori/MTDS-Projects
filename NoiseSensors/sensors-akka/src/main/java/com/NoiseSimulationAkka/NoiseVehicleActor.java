package com.NoiseSimulationAkka;

import java.util.concurrent.ThreadLocalRandom;

public class NoiseVehicleActor extends NoiseObject {
    int posX;
    int posY;
    int actionArea = 10;
    double speedObject;

    public NoiseVehicleActor(int posX, int posY, double speedObject) {
        this.posX = posX;
        this.posY = posY;
        this.speedObject = speedObject;
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestReadingMessage.class, this::onReading)
                .build();
    }

    void onReading(RequestReadingMessage message) {
        if (inSensorArea(message.getPosX(), message.getPosY())) {
            NoiseReadingMessage newReading = new NoiseReadingMessage(generateData(), posX, posY, 1, message.getTimeStamp());
            getSender().tell(newReading, getSelf());
        }
        this.moveObject();
    }

    boolean inSensorArea(int posXS, int posYS) {
        // check if the object is in the area of the sensor
        if( posYS + actionArea > posYS && posYS - actionArea < posYS &&
            posXS + actionArea > posXS && posXS - actionArea < posXS)
            return true;
        System.out.println("Not in area");
        return false;
    }

    @Override
    public void moveObject() {
        this.posX += speedObject;
    }

    @Override
    public double generateData() {
        return ThreadLocalRandom.current().nextDouble(60, 120);
    }

}

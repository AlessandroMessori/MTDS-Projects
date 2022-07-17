package com.NoiseSimulationAkka;

import java.util.concurrent.ThreadLocalRandom;

public class NoisePersonActor extends NoiseObject{
    double posX;
    double posY;
    double speedObject;
    int actionArea = 100;

    public NoisePersonActor(double posX, double posY, double speedObject) {
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

    boolean inSensorArea(double posXS, double posYS) {
        /* Check if the object is in the area of the sensor*/
        if( (this.posY + this.actionArea) > posYS && (this.posY - this.actionArea) < posYS &&
                (this.posX + this.actionArea) > posXS && (this.posX - this.actionArea) < posXS) {
            return true;
        }
        /*TODO 0: check the sensor to be in the area -> modify actionArea to be larger */
        System.out.println("Not in area");
        return false;
    }

    @Override
    /*Move the object randomly in the area
    * TODO 1: make sure it doesn't leave the area or remove object if so + add the limits to the object
    * TODO 2: move the object on both axes*/
    public void moveObject() {
        this.posY += speedObject;
    }

    @Override
    public double generateData() {
        return ThreadLocalRandom.current().nextDouble(20, 80);
    }

}

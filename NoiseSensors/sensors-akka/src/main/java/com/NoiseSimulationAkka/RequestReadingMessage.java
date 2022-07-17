package com.NoiseSimulationAkka;

public class RequestReadingMessage {
    private double posX;
    private double posY;
    private int timeStamp;

    public RequestReadingMessage(double posX, double posY, int timeStamp) {
        this.posX = posX;
        this.posY = posY;
        this.timeStamp = timeStamp;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }
}

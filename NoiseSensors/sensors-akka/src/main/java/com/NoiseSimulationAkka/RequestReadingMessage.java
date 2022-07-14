package com.NoiseSimulationAkka;

public class RequestReadingMessage {
    private int posX;
    private int posY;
    private int timeStamp;

    public RequestReadingMessage(int posX, int posY, int timeStamp) {
        this.posX = posX;
        this.posY = posY;
        this.timeStamp = timeStamp;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
}

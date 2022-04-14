package com.NoiseSimulationAkka;

public class NoiseReadingMessage {

    private double val;
    private double posX;
    private double posY;
    private int objType;
    private int timeStamp;

    public NoiseReadingMessage(double val, double posX, double posY, int objTtype, int timeStamp) {
        this.val = val;
        this.posX = posX;
        this.posY = posY;
        this.timeStamp = timeStamp;

        this.objType = objTtype;
    }

    public double getVal() {
        return this.val;
    }

    public double getPosX() { return this.posX; }

    public double getPosY() { return this.posY; }

    public int getObjType() { return this.objType; }

    @Override
    public String toString() {
        String formattedString = String.format("%.02f", val);
        return "T " + this.timeStamp + " --> " + formattedString + "( " + this.posX + "," + this.posY + " );";
    }

}

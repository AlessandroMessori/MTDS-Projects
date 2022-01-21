package com.NoiseSensors;

public class NoiseReading {

    private double X;
    private double Y;
    private double noiseLevel;

    public NoiseReading() {

    }

    public NoiseReading(double x, double y, double noiseLevel) {
        X = x;
        Y = y;
        this.noiseLevel = noiseLevel;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getNoiseLevel() {
        return noiseLevel;
    }

    public void setNoiseLevel(double noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    @Override
    public String toString() {
        return "latitude: " + this.getX() + " longitude: " + this.getY() + " --> " + this.getNoiseLevel();
    }
}

package com.NoiseSensors;

public class NoiseReading {

    private int index;
    private double val;

    public NoiseReading(int index, double val) {
        this.index = index;
        this.val = val;
    }

    public int getKey() {
        return this.index;
    }

    public double getVal() {
        return this.val;
    }

    @Override
    public String toString() {
        return "T" + this.index + " --> " + this.val;
    }

}

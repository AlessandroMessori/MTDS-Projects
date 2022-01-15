package com.NoiseSensors;

import java.util.LinkedList;
import java.util.Queue;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class VirtualSensorActor extends AbstractActor {

	private Queue<NoiseReading> readings;
	private boolean threshold;
	private int queueSize;
	private double thresholdValue;


	public VirtualSensorActor(int queueSize, double thresholdValue) {
		this.readings = new LinkedList<NoiseReading>();
		this.queueSize = queueSize;
		this.threshold = false;
		this.thresholdValue = thresholdValue;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
        .match(NoiseReading.class, this::onReading)
        .build();
	}

	void onReading(NoiseReading reading) {
		this.readings.add(reading);

		if (this.readings.size() > this.queueSize) {
			this.readings.remove();
		}
		
		double movingAvg = this.getMovingAgv();
		this.threshold = movingAvg > thresholdValue;
		

		System.out.println(this.readings.toString());
		System.out.println("Moving Average --> " + this.getMovingAgv());
		System.out.println("Threshold " + (this.threshold ? "" : "Not ")  + "Exceeded \n");

	}
	
	double getMovingAgv() {

		int size = this.readings.size();

		return this.readings
		.stream()
		.mapToDouble(NoiseReading::getNoiseLevel)
		.reduce(0, (subtotal, element) -> subtotal + element / size);
	}

	static Props props() {
		return Props.create(VirtualSensorActor.class);
	}

}

package com.NoiseSensors;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import DataGenerators.RandomDataGenerator;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class NoiseSensors {

	public static void main(String[] args) {

		final double THRESHOLD = 60;
		final int QUEUE_SIZE = 6;

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef virtualSensor = sys.actorOf(Props.create(VirtualSensorActor.class, QUEUE_SIZE, THRESHOLD), "virtualSensor");
		//int index = 0;

		// Generate a random mock reading for the sensor every second

		RandomDataGenerator randomDataGenerator = new RandomDataGenerator(100, 0);
		Timer t = new Timer();
		NoiseReading noiseReading = new NoiseReading();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				randomDataGenerator.generateData(new Runnable() {
					@Override
					public void run() {
						virtualSensor.tell(new NoiseReading(noiseReading.getX(), noiseReading.getY(), noiseReading.getNoiseLevel()), ActorRef.noSender());
					}
				}, noiseReading);
			}
		}, 0, 5000);
	}

//		while (true) {
//			// generates random number
//			float val = ThreadLocalRandom.current().nextInt(0, 100);
//
//			// sends reading to virtual sensor
//			//virtualSensor.tell(, ActorRef.noSender());
//
//			 try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			index++;
//		}
// sys.terminate();
}


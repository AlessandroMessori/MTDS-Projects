package com.NoiseSensors;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import DataGenerators.FileDataGenerator;
import DataGenerators.RandomDataGenerator;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class NoiseSensors {

 	public static void main(String[] args) {

		final double THRESHOLD = 80;
		final int QUEUE_SIZE = 6;

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef virtualSensor = sys.actorOf(Props.create(VirtualSensorActor.class, QUEUE_SIZE, THRESHOLD), "virtualSensor");
		//int index = 0;

		// Generate a random mock reading for the sensor every second
		//RandomDataGenerator randomDataGenerator = new RandomDataGenerator(100, 50);
		//randomDataGenerator.generateData( noiseReading  -> virtualSensor.tell(noiseReading, ActorRef.noSender()) );

		// Generate a random mock reading for the sensor every second
		FileDataGenerator fileDataGenerator = new FileDataGenerator();
		fileDataGenerator.generateData( noiseReading  -> virtualSensor.tell(noiseReading, ActorRef.noSender()));

	}

}


// sys.terminate();



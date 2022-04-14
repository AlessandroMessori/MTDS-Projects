// kafka bridge for simulation
// script simulation for input data

package com.NoiseSimulationAkka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

//    ++wrong data

//    Input parameters:
//    P = number of people -> random generated?
//    V = number of vehicles -> random generated?
//    areaWidth, areaLength (W, L) = width and length of the (rectangular) region where individuals move (in meters)
//    Np = level of noise (dB) produced by each person -> a threshold
//    Nv = level of noise (dB) produced by each vehicle -> a threshold
//    Dp = distance (side of the square area, in meters) affected by the presence of a person
//    Dv = distance (side of the square area, in meters) affected by the presence of a vehicle
//    speedIndividual (Vp) = moving speed for an individual
//    speedVehicle (Vv) = moving speed for a vehicle
//    timeStep (t) = time step (in seconds): the simulation recomputes the position of people and vehicle, and the level of
//    noise of each square meter in the region with a temporal granularity of t (simulated) seconds

public class AkkaNoiseSimulation {
  public static void main(String[] args) {
    final double maxNoiseLevelPeople = 60.0; //60 for a conversation
    final double minNoiseLevelPeople = 20.0; //20 lowest

    final double maxNoiseLevelVehicles = 100.0; //100 truck at 50mph
    final double minNoiseLevelVehicles = 60.0; //60 normal car at 50mph

    int areaWidth = 1000;
    int areaLength = 1000;

    int timeStep = 1000;
    int timeStamp = 0;

    int posX = ThreadLocalRandom.current().nextInt(0, areaLength / 2);
    int posY = ThreadLocalRandom.current().nextInt(0, areaWidth / 2);

    final int QUEUE_SIZE = 6;

    final ActorSystem sys = ActorSystem.create("System");
    /* TODO: Add multiple sensors
    *  TODO: Randomize the positions better*/
    final ActorRef simulatedSensor = sys.actorOf(Props.create(SensorObjectActor.class, posX, posY, QUEUE_SIZE,
            maxNoiseLevelPeople, minNoiseLevelPeople, maxNoiseLevelVehicles, minNoiseLevelVehicles), "simulatedSensor");

    HashMap<String, ActorRef> simulatedPersons = new HashMap<String, ActorRef>();
    HashMap<String, ActorRef> simulatedVehicles = new HashMap<String, ActorRef>();

    for(int i = 0 ; i < 3; i++){
      ActorRef newActor = sys.actorOf(Props.create(NoisePersonActor.class, posX + i, posY,
              ThreadLocalRandom.current().nextDouble(1, 2)), "simulatedPerson" + i);
      simulatedPersons.put("simulatedPerson" + i, newActor);
    }

    for(int i = 0 ; i < 3; i++){
      ActorRef newActor = sys.actorOf(Props.create(NoiseVehicleActor.class, posX + i, posY,
              ThreadLocalRandom.current().nextDouble(1, 2)),"simulatedVehicle" + i);
      simulatedVehicles.put("simulatedVehicle" + i, newActor);
    }


    while (true) {

      for (ActorRef actorP : simulatedPersons.values() ) {
        actorP.tell(new RequestReadingMessage(posX, posY, timeStamp), simulatedSensor);
      }
      for (ActorRef actorV : simulatedVehicles.values()) {
        actorV.tell(new RequestReadingMessage(posX, posY, timeStamp), simulatedSensor);
      }
      System.out.println("Checking all objects...");

      try {
        Thread.sleep(timeStep);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      timeStamp++;
    }
  }
}


